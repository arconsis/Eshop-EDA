package com.arconsis.domain.orders

import com.arconsis.data.inventory.InventoryRepository
import com.arconsis.data.outboxevents.OutboxEventsRepository
import com.arconsis.data.processedevents.ProcessedEventsRepository
import com.arconsis.data.shipments.ShipmentsRepository
import com.arconsis.domain.ordersvalidations.OrderValidation
import com.arconsis.domain.ordersvalidations.OrderValidationStatus
import com.arconsis.domain.ordersvalidations.toCreateOutboxEvent
import com.arconsis.domain.processedevents.ProcessedEvent
import com.arconsis.domain.shipments.*
import com.fasterxml.jackson.databind.ObjectMapper
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import java.time.Instant
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrdersService(
    private val shipmentsRepository: ShipmentsRepository,
    private val inventoryRepository: InventoryRepository,
    private val outboxEventsRepository: OutboxEventsRepository,
    private val objectMapper: ObjectMapper,
    private val sessionFactory: Mutiny.SessionFactory,
    private val processedEventsRepository: ProcessedEventsRepository
) {

    fun handleOrderEvents(eventId: UUID, order: Order): Uni<Void> {
        return when (order.status) {
            OrderStatus.REQUESTED -> handleOrderPending(eventId, order)
            OrderStatus.PAID -> handleOrderPaid(eventId, order)
            OrderStatus.PAYMENT_FAILED -> handleOrderPaymentFailed(eventId, order)
            else -> Uni.createFrom().voidItem()
        }
    }

    private fun handleOrderPending(eventId: UUID, order: Order): Uni<Void> {
        return sessionFactory.withTransaction { session, _ ->
            processedEventsRepository.getEvent(eventId, session)
                .flatMap { event ->
                    if (event != null) Uni.createFrom().voidItem()
                    inventoryRepository.reserveProductStock(order.productId, order.quantity, session)
                }
                .createOrderValidation(order)
                .createOrderValidationEvent(session)
                .createProceedEvent(eventId, session)
                .map {
                    null
                }

        }
    }

    private fun handleOrderPaid(eventId: UUID, order: Order): Uni<Void> {
        return sessionFactory.withTransaction { session, _ ->
            processedEventsRepository.getEvent(eventId, session)
                .flatMap { event ->
                    if (event != null) Uni.createFrom().voidItem()
                    val createShipment = CreateShipment(
                        orderId = order.id,
                        userId = order.userId,
                        status = ShipmentStatus.PREPARING_SHIPMENT
                    )
                    shipmentsRepository.createShipment(createShipment, session)
                }
                .updateShipment(session)
                .createShipmentEvent(session)
                .createProceedEvent(eventId, session)
                .map {
                    null
                }
        }
    }

    private fun handleOrderPaymentFailed(eventId: UUID, order: Order): Uni<Void> =
        sessionFactory.withTransaction { session, _ ->
            processedEventsRepository.getEvent(eventId, session)
                .flatMap { event ->
                    if (event != null) Uni.createFrom().voidItem()
                    inventoryRepository.increaseProductStock(order.productId, order.quantity)
                }
                .createProceedEvent(eventId, session)
                .flatMap {
                    Uni.createFrom().voidItem()
                }
        }


    private fun Uni<Boolean>.createOrderValidation(order: Order) = map { stockUpdated ->
        val orderValidation = OrderValidation(
            productId = order.productId,
            quantity = order.quantity,
            orderId = order.id,
            userId = order.userId,
            status = if (stockUpdated) OrderValidationStatus.VALIDATED else OrderValidationStatus.INVALID
        )
        orderValidation
    }

    private fun Uni<OrderValidation>.createOrderValidationEvent(session: Mutiny.Session) = flatMap { orderValidation ->
        val createOutboxEvent = orderValidation.toCreateOutboxEvent(objectMapper)
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

    private fun Uni<Shipment>.updateShipment(session: Mutiny.Session) = flatMap { shipment ->
        val updateShipment = UpdateShipment(
            shipment.id,
            ShipmentStatus.SHIPPED
        )
        shipmentsRepository.updateShipment(updateShipment, session)
    }

    private fun Uni<Shipment>.createShipmentEvent(session: Mutiny.Session) = flatMap { shipment ->
        val createOutboxEvent = shipment.toCreateOutboxEvent(objectMapper)
        outboxEventsRepository.createEvent(createOutboxEvent, session)
    }
}