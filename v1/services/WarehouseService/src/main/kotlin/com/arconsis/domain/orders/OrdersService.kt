package com.arconsis.domain.orders

import com.arconsis.data.inventory.InventoryRepository
import com.arconsis.data.shipments.ShipmentsRepository
import com.arconsis.domain.ordersvalidations.OrderValidation
import com.arconsis.domain.ordersvalidations.OrderValidationStatus
import com.arconsis.domain.shipments.*
import io.smallrye.mutiny.Uni
import io.smallrye.reactive.messaging.MutinyEmitter
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Channel
import org.eclipse.microprofile.reactive.messaging.Incoming
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrdersService(
    @Channel("shipment-out") private val shipmentEmitter: MutinyEmitter<Record<String, Shipment>>,
    @Channel("order-validation-out") private val orderValidationEmitter: MutinyEmitter<Record<String, OrderValidation>>,
    private val shipmentsRepository: ShipmentsRepository,
    private val inventoryRepository: InventoryRepository,
) {

    @Incoming("order-in")
    fun consumeOrderEvents(orderRecord: Record<String, Order>): Uni<Void> {
        val order = orderRecord.value()

        return when (order.status) {
            OrderStatus.PENDING -> handleOrderPending(order)
            OrderStatus.PAID -> handleOrderPaid(order)
            OrderStatus.PAYMENT_FAILED -> handleOrderPaymentFailed(order)
            else -> Uni.createFrom().voidItem()
        }
    }

    private fun handleOrderPending(order: Order): Uni<Void> {
        return inventoryRepository.reserveProductStock(order.productId, order.quantity).onItem()
            .transformToUni { stockUpdated ->
                val orderValidation = OrderValidation(
                    productId = order.productId,
                    quantity = order.quantity,
                    orderId = order.id,
                    userId = order.userId,
                    status = if (stockUpdated) OrderValidationStatus.VALID else OrderValidationStatus.INVALID
                )

                orderValidationEmitter.send(Record.of(order.id.toString(), orderValidation))
            }
    }

    private fun handleOrderPaid(order: Order): Uni<Void> {
        return this.shipmentsRepository.createShipment(
            CreateShipment(
                orderId = order.id,
                userId = order.userId,
                status = ShipmentStatus.PREPARING_SHIPMENT
            )
        ).flatMap { shipment ->
            this.shipmentsRepository.updateShipment(
                UpdateShipment(
                    shipment.id,
                    ShipmentStatus.OUT_FOR_SHIPMENT
                )
            )
        }.flatMap { shipment ->
            shipmentEmitter.send(shipment.toShipmentRecord())
        }
    }

    private fun handleOrderPaymentFailed(order: Order): Uni<Void> {
        return inventoryRepository.increaseProductStock(order.productId, order.quantity)
            .flatMap {
                Uni.createFrom().voidItem()
            }
    }
}