package com.arconsis.domain.orders

import com.arconsis.data.outboxevents.OutboxEventsRepository
import com.arconsis.data.payments.PaymentsRepository
import com.arconsis.data.payments.toCreatePayment
import com.arconsis.domain.payments.Payment
import com.fasterxml.jackson.databind.ObjectMapper
import io.smallrye.mutiny.Uni
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrdersService(
    private val paymentsRepository: PaymentsRepository,
    private val outboxEventsRepository: OutboxEventsRepository,
    private val objectMapper: ObjectMapper,
) {

    fun handleOrderEvents(order: Order): Uni<Void> {
        return when (order.status) {
            OrderStatus.VALID -> handleValidOrder(order)
            else -> Uni.createFrom().voidItem()
        }
    }

    private fun handleValidOrder(order: Order): Uni<Void> {
        return paymentsRepository.createPayment(order.toCreatePayment())
            .createOutboxEvent()
            .map {
                null
            }
    }

    private fun Uni<Payment>.createOutboxEvent() = flatMap { payment ->
        outboxEventsRepository.createEvent(payment)
    }
}