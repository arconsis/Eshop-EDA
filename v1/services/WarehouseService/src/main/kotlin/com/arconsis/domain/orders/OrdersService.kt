package com.arconsis.domain.orders

import com.arconsis.data.inventory.InventoryRepository
import com.arconsis.data.shipments.ShipmentsRepository
import com.arconsis.domain.ordersvalidations.OrderValidation
import com.arconsis.domain.ordersvalidations.OrderValidationStatus
import com.arconsis.domain.shipments.*
import io.smallrye.mutiny.coroutines.awaitSuspending
import io.smallrye.reactive.messaging.MutinyEmitter
import io.smallrye.reactive.messaging.kafka.Record
import kotlinx.coroutines.delay
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
    suspend fun consumeOrderEvents(orderRecord: Record<String, Order>) {
        val order = orderRecord.value()

        when (order.status) {
            OrderStatus.PENDING -> handleOrderPending(order)
            OrderStatus.PAID -> handleOrderPaid(order)
            else -> return
        }
    }

    private suspend fun handleOrderPending(order: Order) {
        val stockUpdated = inventoryRepository.reserveProductStock(order.productId, order.quantity)

        val orderValidation = OrderValidation(
            productId = order.productId,
            quantity = order.quantity,
            orderId = order.id,
            userId = order.userId,
            status = if (stockUpdated) OrderValidationStatus.VALID else OrderValidationStatus.INVALID
        )

        orderValidationEmitter.send(Record.of(order.id.toString(), orderValidation)).awaitSuspending()
    }

    private suspend fun handleOrderPaid(order: Order) {
        var shipment = this.shipmentsRepository.createShipment(
            CreateShipment(
                orderId = order.id,
                userId = order.userId,
                status = ShipmentStatus.PREPARING_SHIPMENT
            )
        )
        // TODO: Simulating shipment. Check if there is an alternative for this
        delay(10000)
        shipment = this.shipmentsRepository.updateShipment(UpdateShipment(shipment.id, ShipmentStatus.OUT_FOR_SHIPMENT))
        shipmentEmitter.send(shipment.toShipmentRecord()).awaitSuspending()
    }
}