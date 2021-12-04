package com.arconsis.domain.shipments

import com.arconsis.data.orders.OrdersRepository
import com.arconsis.data.outboxevents.OutboxEventsRepository
import com.arconsis.data.processedevents.ProcessedEventsRepository
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrderStatus
import com.arconsis.domain.orders.toCreateOutboxEvent
import com.arconsis.domain.processedevents.ProcessedEvent
import com.fasterxml.jackson.databind.ObjectMapper
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import java.time.Instant
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ShipmentsService(
    private val ordersRepository: OrdersRepository,
    private val outboxEventsRepository: OutboxEventsRepository,
    private val processedEventsRepository: ProcessedEventsRepository,
    private val sessionFactory: Mutiny.SessionFactory,
    private val objectMapper: ObjectMapper,
) {
    fun handleShipmentEvents(eventId: UUID, shipment: Shipment): Uni<Void> {
        return when (shipment.status) {
            ShipmentStatus.DELIVERED -> handleDeliveredShipment(eventId, shipment)
            ShipmentStatus.SHIPPED -> handleOutForShipment(eventId, shipment)
            else -> return Uni.createFrom().voidItem()
        }
    }

    private fun handleDeliveredShipment(eventId: UUID, shipment: Shipment): Uni<Void> {
        return sessionFactory.withTransaction { session, _ ->
            processedEventsRepository.getEvent(eventId, session)
                .updateOrder(shipment, OrderStatus.COMPLETED, session)
                .createOutboxEvent(session)
                .createProceedEvent(eventId, session)
                .map {
                    null
                }
        }
    }

    private fun handleOutForShipment(eventId: UUID, shipment: Shipment): Uni<Void> {
        return sessionFactory.withTransaction { session, _ ->
            processedEventsRepository.getEvent(eventId, session)
                .updateOrder(shipment, OrderStatus.SHIPPED, session)
                .createOutboxEvent(session)
                .createProceedEvent(eventId, session)
                .map {
                    null
                }
        }
    }

    private fun Uni<ProcessedEvent?>.updateOrder(
        shipment: Shipment,
        orderStatus: OrderStatus,
        session: Mutiny.Session
    ) = flatMap { event ->
        if (event != null) Uni.createFrom().voidItem()
        ordersRepository.updateOrder(shipment.orderId, orderStatus, session)
    }

    private fun Uni<Order>.createOutboxEvent(session: Mutiny.Session) = flatMap { order ->
        val createOutboxEvent = order.toCreateOutboxEvent(objectMapper)
        outboxEventsRepository.createEvent(createOutboxEvent, session)
    }

    private fun <T> Uni<T>.createProceedEvent(eventId: UUID, session: Mutiny.Session) =
        flatMap {
            val proceedEvent = ProcessedEvent(
                eventId = eventId,
                processedAt = Instant.now()
            )
            processedEventsRepository.createEvent(proceedEvent, session)
        }
}