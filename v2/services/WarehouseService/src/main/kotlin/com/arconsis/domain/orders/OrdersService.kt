package com.arconsis.domain.orders

import com.arconsis.data.inventory.InventoryRepository
import com.arconsis.data.outboxevents.OutboxEventsRepository
import com.arconsis.data.shipments.ShipmentsRepository
import com.arconsis.domain.ordersvalidations.OrderValidation
import com.arconsis.domain.ordersvalidations.OrderValidationStatus
import com.arconsis.domain.ordersvalidations.toCreateOutboxEvent
import com.arconsis.domain.shipments.CreateShipment
import com.arconsis.domain.shipments.ShipmentStatus
import com.arconsis.domain.shipments.UpdateShipment
import com.arconsis.domain.shipments.toCreateOutboxEvent
import com.fasterxml.jackson.databind.ObjectMapper
import io.smallrye.mutiny.coroutines.awaitSuspending
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class OrdersService(
    private val shipmentsRepository: ShipmentsRepository,
    private val inventoryRepository: InventoryRepository,
    private val outboxEventsRepository: OutboxEventsRepository,
    private val objectMapper: ObjectMapper,
) {
    @Transactional
    suspend fun handleOrderEvents(order: Order) {
        when (order.status) {
            OrderStatus.PENDING -> handleOrderPending(order)
            OrderStatus.PAID -> handleOrderPaid(order)
            OrderStatus.PAYMENT_FAILED -> handleOrderPaymentFailed(order)
            else -> null
        }
    }

    private suspend fun handleOrderPending(order: Order) {
        val stockUpdated = inventoryRepository.reserveProductStock(order.productId, order.quantity).awaitSuspending()
        val orderValidation = OrderValidation(
            productId = order.productId,
            quantity = order.quantity,
            orderId = order.id,
            userId = order.userId,
            status = if (stockUpdated) OrderValidationStatus.VALID else OrderValidationStatus.INVALID
        )
        val createOutboxEvent = orderValidation.toCreateOutboxEvent(objectMapper)
        outboxEventsRepository.createEvent(createOutboxEvent).awaitSuspending()
    }

    private suspend fun handleOrderPaid(order: Order) {
        val shipment = shipmentsRepository.createShipment(
            CreateShipment(
                orderId = order.id,
                userId = order.userId,
                status = ShipmentStatus.PREPARING_SHIPMENT
            )
        ).awaitSuspending()
        val updatedShipment = shipmentsRepository.updateShipment(
            UpdateShipment(
                shipment.id,
                ShipmentStatus.OUT_FOR_SHIPMENT
            )
        ).awaitSuspending()
        val createOutboxEvent = updatedShipment.toCreateOutboxEvent(objectMapper)
        outboxEventsRepository.createEvent(createOutboxEvent).awaitSuspending()
    }

    private suspend fun handleOrderPaymentFailed(order: Order) =
        inventoryRepository.increaseProductStock(order.productId, order.quantity).awaitSuspending()
}