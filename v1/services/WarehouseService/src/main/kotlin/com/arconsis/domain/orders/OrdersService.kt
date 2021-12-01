package com.arconsis.domain.orders

import com.arconsis.common.retryWithBackoff
import com.arconsis.data.inventory.InventoryRepository
import com.arconsis.data.shipments.ShipmentsRepository
import com.arconsis.domain.ordersvalidations.OrderValidation
import com.arconsis.domain.ordersvalidations.OrderValidationStatus
import com.arconsis.domain.shipments.*
import io.smallrye.mutiny.Uni
import io.smallrye.reactive.messaging.MutinyEmitter
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Channel
import org.jboss.logging.Logger
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrdersService(
    @Channel("shipment-out") private val shipmentEmitter: MutinyEmitter<Record<String, Shipment>>,
    @Channel("order-validation-out") private val orderValidationEmitter: MutinyEmitter<Record<String, OrderValidation>>,
    private val shipmentsRepository: ShipmentsRepository,
    private val inventoryRepository: InventoryRepository,
    private val logger: Logger
) {
    fun handleOrderEvents(order: Order): Uni<Void> {
        return when (order.status) {
            OrderStatus.REQUESTED -> handleOrderPending(order)
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
                    status = if (stockUpdated) OrderValidationStatus.VALIDATED else OrderValidationStatus.INVALID
                )
                orderValidationEmitter.send(Record.of(order.id.toString(), orderValidation))
            }
    }

    private fun handleOrderPaid(order: Order): Uni<Void> {
        return shipmentsRepository.createShipment(
            CreateShipment(
                orderId = order.id,
                userId = order.userId,
                status = ShipmentStatus.PREPARING_SHIPMENT
            )
        )
            .handleShipmentError(order, null)
            .updateShipment(order)
            .flatMap { shipment -> sendShipmentEvent(shipment.toShipmentRecord()) }
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

    private fun Uni<Shipment>.updateShipment(order: Order) = flatMap { shipment ->
        if (shipment?.id == null) {
            throw Exception("Shipment failed")
        }
        shipmentsRepository.updateShipment(
            UpdateShipment(
                shipment.id,
                ShipmentStatus.SHIPPED
            )
        )
            .handleShipmentError(order, shipment.id)
    }

    private fun Uni<Shipment>.handleShipmentError(order: Order, shipmentId: UUID?) = retryWithBackoff()
        .onFailure()
        .call { _ -> sendShipmentEvent(order.toFailedShipment(shipmentId).toShipmentRecord()) }

    private fun sendShipmentEvent(shipmentRecord: Record<String, Shipment>): Uni<Void> {
        logger.info("Send shipment record $shipmentRecord")
        return shipmentEmitter.send(shipmentRecord)
    }
}