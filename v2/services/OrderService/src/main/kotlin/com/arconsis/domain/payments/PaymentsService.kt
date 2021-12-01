package com.arconsis.domain.payments

import com.arconsis.data.orders.OrdersRepository
import com.arconsis.data.outboxevents.OutboxEventsRepository
import com.arconsis.domain.orders.OrderStatus
import com.arconsis.domain.orders.toCreateOutboxEvent
import com.fasterxml.jackson.databind.ObjectMapper
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PaymentsService(
    private val ordersRepository: OrdersRepository,
    private val outboxEventsRepository: OutboxEventsRepository,
    private val sessionFactory: Mutiny.SessionFactory,
    private val objectMapper: ObjectMapper,
) {
    fun handlePaymentEvents(payment: Payment): Uni<Void> {
        return when (payment.status) {
            PaymentStatus.SUCCEED -> handleSucceedPayment(payment)
            PaymentStatus.FAILED -> handleFailedPayment(payment)
        }
    }

    private fun handleSucceedPayment(payment: Payment): Uni<Void> {
        return sessionFactory.withTransaction { session, _ ->
            ordersRepository.updateOrder(payment.orderId, OrderStatus.PAID, session)
                .flatMap { order ->
                    val createOutboxEvent = order.toCreateOutboxEvent(objectMapper)
                    outboxEventsRepository.createEvent(createOutboxEvent, session)
                }
                .map {
                    null
                }
        }
    }

    private fun handleFailedPayment(payment: Payment): Uni<Void> {
        return sessionFactory.withTransaction { session, _ ->
            ordersRepository.updateOrder(payment.orderId, OrderStatus.PAYMENT_FAILED, session)
                .flatMap { order ->
                    val createOutboxEvent = order.toCreateOutboxEvent(objectMapper)
                    outboxEventsRepository.createEvent(createOutboxEvent, session)
                }
                .map {
                    null
                }
        }
    }
}