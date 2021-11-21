package com.arconsis.domain.shipments

import com.arconsis.data.orders.OrdersRepository
import com.arconsis.data.outboxevents.OutboxEventsRepository
import com.arconsis.domain.orders.OrderStatus
import com.arconsis.domain.orders.toCreateOutboxEvent
import com.fasterxml.jackson.databind.ObjectMapper
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.coroutines.awaitSuspending
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class ShipmentsService(
    private val ordersRepository: OrdersRepository,
    private val outboxEventsRepository: OutboxEventsRepository,
    private val objectMapper: ObjectMapper,
) {
    @Transactional
    suspend fun handleShipmentEvents(shipment: Shipment) {
        when (shipment.status) {
            ShipmentStatus.SHIPPED -> {
                val order = ordersRepository.updateOrder(shipment.orderId, OrderStatus.COMPLETED).awaitSuspending()
                val createOutboxEvent = order.toCreateOutboxEvent(objectMapper)
                outboxEventsRepository.createEvent(createOutboxEvent).awaitSuspending()

            }
            ShipmentStatus.OUT_FOR_SHIPMENT -> {
                val order = ordersRepository.updateOrder(shipment.orderId, OrderStatus.OUT_FOR_SHIPMENT).awaitSuspending()
                val createOutboxEvent = order.toCreateOutboxEvent(objectMapper)
                outboxEventsRepository.createEvent(createOutboxEvent).awaitSuspending()
            }
            else -> null
        }
    }
}