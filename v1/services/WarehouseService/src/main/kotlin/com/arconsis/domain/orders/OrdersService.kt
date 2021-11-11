package com.arconsis.domain.orders

import com.arconsis.data.inventory.InventoryRepository
import com.arconsis.data.shipments.ShipmentsRepository
import com.arconsis.domain.ordersvalidations.OrderValidation
import com.arconsis.domain.ordersvalidations.OrderValidationStatus
import com.arconsis.domain.shipments.*
import io.smallrye.reactive.messaging.annotations.Blocking
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Channel
import org.eclipse.microprofile.reactive.messaging.Emitter
import org.eclipse.microprofile.reactive.messaging.Incoming
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class OrdersService(
    @Channel("shipment-out") private val shipmentEmitter: Emitter<Record<String, Shipment>>,
    @Channel("order-validation-out") private val orderValidationEmitter: Emitter<Record<String, OrderValidation>>,
    private val shipmentsRepository: ShipmentsRepository,
    private val inventoryRepository: InventoryRepository,
) {

    @Incoming("order-in")
    @Blocking
    @Transactional
    fun consumeOrderEvents(orderRecord: Record<String, Order>): CompletionStage<Void> {
        val order = orderRecord.value()

        return when (order.status) {
            OrderStatus.PENDING -> handleOrderPending(order)
            OrderStatus.PAID -> handleOrderPaid(order)
            else -> CompletableFuture.completedStage(null)
        }
    }

    private fun handleOrderPending(order: Order): CompletionStage<Void> {
        val stockUpdated = inventoryRepository.reserveProductStock(order.productId, order.quantity)

        val orderValidation = OrderValidation(
            productId = order.productId,
            quantity = order.quantity,
            orderId = order.id,
            userId = order.userId,
            status = if (stockUpdated) OrderValidationStatus.VALID else OrderValidationStatus.INVALID
        )

        return orderValidationEmitter.send(Record.of(order.id.toString(), orderValidation))
    }

    private fun handleOrderPaid(order: Order): CompletionStage<Void> {
        var shipment = this.shipmentsRepository.createShipment(
            CreateShipment(
                orderId = order.id,
                userId = order.userId,
                status = ShipmentStatus.PREPARING_SHIPMENT
            )
        )
        // TODO: Simulating shipment. Check if there is an alternative for this
        Thread.sleep(10000)
        shipment = this.shipmentsRepository.updateShipment(UpdateShipment(shipment.id, ShipmentStatus.OUT_FOR_SHIPMENT))
        return shipmentEmitter.send(shipment.toShipmentRecord())
    }
}