package com.arconsis.domain.shipments

import com.arconsis.data.orders.OrdersRepository
import com.arconsis.data.outboxevents.OutboxEventsRepository
import com.arconsis.domain.orders.OrderStatus
import com.arconsis.domain.orders.toCreateOutboxEvent
import com.fasterxml.jackson.databind.ObjectMapper
import io.smallrye.mutiny.Uni
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class ShipmentsService(
    private val ordersRepository: OrdersRepository,
    private val outboxEventsRepository: OutboxEventsRepository,
    private val objectMapper: ObjectMapper,
) {
    @Transactional
    fun handleShipmentEvents(shipment: Shipment): Uni<Void> {
        return when (shipment.status) {
            ShipmentStatus.SHIPPED -> handleShippedShipment(shipment)
            ShipmentStatus.OUT_FOR_SHIPMENT -> handleOutForShipment(shipment)
            else -> return Uni.createFrom().voidItem()
        }
    }

    private fun handleShippedShipment(shipment: Shipment): Uni<Void> {
        return ordersRepository.updateOrder(shipment.orderId, OrderStatus.COMPLETED)
            .flatMap { order ->
                val createOutboxEvent = order.toCreateOutboxEvent(objectMapper)
                outboxEventsRepository.createEvent(createOutboxEvent)
            }
            .map {
                null
            }
    }

    private fun handleOutForShipment(shipment: Shipment): Uni<Void> {
        return ordersRepository.updateOrder(shipment.orderId, OrderStatus.OUT_FOR_SHIPMENT)
            .flatMap { order ->
                val createOutboxEvent = order.toCreateOutboxEvent(objectMapper)
                outboxEventsRepository.createEvent(createOutboxEvent)
            }
            .map {
                null
            }
    }
}