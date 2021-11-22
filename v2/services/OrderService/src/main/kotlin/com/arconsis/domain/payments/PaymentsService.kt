package com.arconsis.domain.payments

import com.arconsis.data.orders.OrdersRepository
import com.arconsis.data.outboxevents.OutboxEventsRepository
import com.arconsis.domain.orders.OrderStatus
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PaymentsService(
    private val ordersRepository: OrdersRepository,
    private val outboxEventsRepository: OutboxEventsRepository,
    private val sessionFactory: Mutiny.SessionFactory
) {
    fun handlePaymentEvents(payment: Payment): Uni<Void> {
        return when (payment.status) {
            PaymentStatus.SUCCESS -> handleSucceedPayment(payment)
            PaymentStatus.FAILED -> handleFailedPayment(payment)
        }
    }

    private fun handleSucceedPayment(payment: Payment): Uni<Void> {
        return sessionFactory.withTransaction { session, _ ->
            ordersRepository.updateOrder(payment.orderId, OrderStatus.PAID, session)
                .flatMap { order ->
                    outboxEventsRepository.createEvent(order, session)
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
                    outboxEventsRepository.createEvent(order, session)
                }
                .map {
                    null
                }
        }
    }
}