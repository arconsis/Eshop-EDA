package com.arconsis.domain.payments

import com.arconsis.data.orders.OrdersRepository
import com.arconsis.data.outboxevents.OutboxEventsRepository
import com.arconsis.domain.orders.OrderStatus
import com.arconsis.domain.orders.toCreateOutboxEvent
import com.fasterxml.jackson.databind.ObjectMapper
import io.smallrye.mutiny.Uni
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class PaymentsService(
    private val ordersRepository: OrdersRepository,
    private val outboxEventsRepository: OutboxEventsRepository,
    private val objectMapper: ObjectMapper,
) {
    @Transactional
    fun handlePaymentEvents(payment: Payment): Uni<Void> {
        return when (payment.status) {
            PaymentStatus.SUCCESS -> handleSucceedPayment(payment)
            PaymentStatus.FAILED -> handleFailedPayment(payment)
        }
    }

    private fun handleSucceedPayment(payment: Payment): Uni<Void> {
        return ordersRepository.updateOrder(payment.orderId, OrderStatus.PAID)
            .flatMap { order ->
                val createOutboxEvent = order.toCreateOutboxEvent(objectMapper)
                outboxEventsRepository.createEvent(createOutboxEvent)
            }
            .map {
                null
            }
    }

    private fun handleFailedPayment(payment: Payment): Uni<Void> {
        return ordersRepository.updateOrder(payment.orderId, OrderStatus.PAYMENT_FAILED)
            .flatMap { order ->
                val createOutboxEvent = order.toCreateOutboxEvent(objectMapper)
                outboxEventsRepository.createEvent(createOutboxEvent)
            }
            .map {
                null
            }
    }
}