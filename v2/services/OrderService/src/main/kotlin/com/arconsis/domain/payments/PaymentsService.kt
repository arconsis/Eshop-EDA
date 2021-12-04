package com.arconsis.domain.payments

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
class PaymentsService(
    private val ordersRepository: OrdersRepository,
    private val outboxEventsRepository: OutboxEventsRepository,
    private val processedEventsRepository: ProcessedEventsRepository,
    private val sessionFactory: Mutiny.SessionFactory,
    private val objectMapper: ObjectMapper,
) {
    fun handlePaymentEvents(eventId: UUID, payment: Payment): Uni<Void> {
        return when (payment.status) {
            PaymentStatus.SUCCEED -> handleSucceedPayment(eventId, payment)
            PaymentStatus.FAILED -> handleFailedPayment(eventId, payment)
        }
    }

    private fun handleSucceedPayment(eventId: UUID, payment: Payment): Uni<Void> {
        return sessionFactory.withTransaction { session, _ ->
            processedEventsRepository.getEvent(eventId, session)
                .updateOrder(payment, OrderStatus.PAID, session)
                .createOutboxEvent(session)
                .createProceedEvent(eventId, session)
                .map {
                    null
                }
        }
    }

    private fun handleFailedPayment(eventId: UUID, payment: Payment): Uni<Void> {
        return sessionFactory.withTransaction { session, _ ->
            processedEventsRepository.getEvent(eventId, session)
                .updateOrder(payment, OrderStatus.PAYMENT_FAILED, session)
                .createOutboxEvent(session)
                .createProceedEvent(eventId, session)
                .map {
                    null
                }
        }
    }

    private fun Uni<ProcessedEvent?>.updateOrder(
        payment: Payment,
        orderStatus: OrderStatus,
        session: Mutiny.Session
    ) = flatMap { event ->
        if (event != null) Uni.createFrom().voidItem()
        ordersRepository.updateOrder(payment.orderId, orderStatus, session)
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