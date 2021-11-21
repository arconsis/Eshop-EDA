package com.arconsis.domain.orders

import com.arconsis.data.PaymentsRepository
import com.arconsis.data.outboxevents.OutboxEventsRepository
import com.arconsis.data.toCreatePayment
import com.arconsis.domain.payments.toCreateOutboxEvent
import com.fasterxml.jackson.databind.ObjectMapper
import io.smallrye.mutiny.coroutines.awaitSuspending
import kotlinx.coroutines.delay
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class OrdersService(
    private val paymentsRepository: PaymentsRepository,
    private val outboxEventsRepository: OutboxEventsRepository,
    private val objectMapper: ObjectMapper,
) {

    @Transactional
    suspend fun handleOrderEvents(order: Order) {
        when (order.status) {
            OrderStatus.VALID -> {
                // TODO: simulate API call
                delay(5000)
                val createPaymentDto = order.toCreatePayment()
                val payment = paymentsRepository.createPayment(createPaymentDto)
                val createOutboxEvent = payment.toCreateOutboxEvent(objectMapper)
                outboxEventsRepository.createEvent(createOutboxEvent).awaitSuspending()
            }
            else -> null
        }
    }
}