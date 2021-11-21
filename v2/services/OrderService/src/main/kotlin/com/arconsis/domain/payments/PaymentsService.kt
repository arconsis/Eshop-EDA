package com.arconsis.domain.payments

import com.arconsis.data.orders.OrdersRepository
import com.arconsis.data.outboxevents.OutboxEventsRepository
import com.arconsis.domain.orders.OrderStatus
import com.arconsis.domain.orders.toCreateOutboxEvent
import com.fasterxml.jackson.databind.ObjectMapper
import io.smallrye.mutiny.coroutines.awaitSuspending
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class PaymentsService(
    private val ordersRepository: OrdersRepository,
    private val outboxEventsRepository: OutboxEventsRepository,
    private val objectMapper: ObjectMapper,
) {
    @Transactional
    suspend fun handlePaymentEvents(payment: Payment) {
        when (payment.status) {
            PaymentStatus.SUCCESS -> {
                val order = ordersRepository.updateOrder(payment.orderId, OrderStatus.PAID).awaitSuspending()
                val createOutboxEvent = order.toCreateOutboxEvent(objectMapper)
                outboxEventsRepository.createEvent(createOutboxEvent).awaitSuspending()

            }
            PaymentStatus.FAILED -> {
                val order = ordersRepository.updateOrder(payment.orderId, OrderStatus.PAYMENT_FAILED).awaitSuspending()
                val createOutboxEvent = order.toCreateOutboxEvent(objectMapper)
                outboxEventsRepository.createEvent(createOutboxEvent).awaitSuspending()
            }
        }
    }
}