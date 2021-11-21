package com.arconsis.domain.orders

import com.arconsis.data.inventory.InventoryRepository
import com.arconsis.data.outboxevents.OutboxEventsRepository
import com.arconsis.data.shipments.ShipmentsRepository
import com.arconsis.domain.ordersvalidations.OrderValidation
import com.arconsis.domain.ordersvalidations.OrderValidationStatus
import com.arconsis.domain.ordersvalidations.toCreateOutboxEvent
import com.arconsis.domain.shipments.*
import com.fasterxml.jackson.databind.ObjectMapper
import io.smallrye.mutiny.Uni
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
            .createOrderValidation(order)
            .createOrderValidationEvent()
            .map {
                null
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
            .updateShipment()
            .createShipmentEvent()
            .map {
                null
            }
    }

    private fun handleOrderPaymentFailed(order: Order): Uni<Void> =
        inventoryRepository.increaseProductStock(order.productId, order.quantity)
            .flatMap {
                Uni.createFrom().voidItem()
            }

    private fun Uni<Boolean>.createOrderValidation(order: Order) = map { stockUpdated ->
        val orderValidation = OrderValidation(
            productId = order.productId,
            quantity = order.quantity,
            orderId = order.id,
            userId = order.userId,
            status = if (stockUpdated) OrderValidationStatus.VALID else OrderValidationStatus.INVALID
        )
        orderValidation
    }

    private fun Uni<OrderValidation>.createOrderValidationEvent() = flatMap { orderValidation ->
        val createOutboxEvent = orderValidation.toCreateOutboxEvent(objectMapper)
        outboxEventsRepository.createEvent(createOutboxEvent)
    }

    private fun Uni<Shipment>.updateShipment() = flatMap { shipment ->
        shipmentsRepository.updateShipment(
            UpdateShipment(
                shipment.id,
                ShipmentStatus.OUT_FOR_SHIPMENT
            )
        )
    }

    private fun Uni<Shipment>.createShipmentEvent() = flatMap { shipment ->
        val createOutboxEvent = shipment.toCreateOutboxEvent(objectMapper)
        outboxEventsRepository.createEvent(createOutboxEvent)
    }
}