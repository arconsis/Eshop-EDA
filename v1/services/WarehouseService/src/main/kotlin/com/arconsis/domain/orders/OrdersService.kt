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
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrdersService(
    @Channel("shipment-out") private val shipmentEmitter: MutinyEmitter<Record<String, Shipment>>,
    @Channel("order-validation-out") private val orderValidationEmitter: MutinyEmitter<Record<String, OrderValidation>>,
    private val shipmentsRepository: ShipmentsRepository,
    private val inventoryRepository: InventoryRepository,
) {
    fun handleOrderEvents(order: Order): Uni<Void> {
        return when (order.status) {
            OrderStatus.PENDING -> handleOrderPending(order)
            OrderStatus.PAID -> handleOrderPaid(order)
            OrderStatus.PAYMENT_FAILED -> handleOrderPaymentFailed(order)
            else -> Uni.createFrom().voidItem()
        }
    }

    private fun handleOrderPending(order: Order): Uni<Void> {
        return inventoryRepository.reserveProductStock(order.productId, order.quantity)
            .flatMap { stockUpdated ->
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
        )
            .onFailure()
            .invoke { _ -> shipmentEmitter.send(order.toFailedShipment(null).toShipmentRecord()) }
            .flatMap { shipment ->
                if (shipment?.id == null) {
                    throw Exception("Shipment failed")
                }
                this.shipmentsRepository.updateShipment(
                    UpdateShipment(
                        shipment.id,
                        ShipmentStatus.OUT_FOR_SHIPMENT
                    )
                )
                    .onFailure()
                    .invoke { _ -> shipmentEmitter.send(order.toFailedShipment(shipment.id).toShipmentRecord()) }
            }
            .flatMap { shipment -> shipmentEmitter.send(shipment.toShipmentRecord()) }
            .onFailure()
            .recoverWithNull()
    }

    private fun handleOrderPaymentFailed(order: Order): Uni<Void> {
        return inventoryRepository.increaseProductStock(order.productId, order.quantity)
            .onFailure()
            .retry()
            .atMost(3)
            .flatMap {
                Uni.createFrom().voidItem()
            }
    }
}