package com.arconsis.domain.ordersvalidations

import com.arconsis.data.orders.OrdersRepository
import com.arconsis.data.outboxevents.OutboxEventsRepository
import com.arconsis.domain.orders.OrderStatus
import com.arconsis.domain.orders.toCreateOutboxEvent
import com.fasterxml.jackson.databind.ObjectMapper
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.coroutines.awaitSuspending
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class OrderValidationsService(
    private val ordersRepository: OrdersRepository,
    private val outboxEventsRepository: OutboxEventsRepository,
    private val objectMapper: ObjectMapper,
) {

    @Transactional
    suspend fun handleOrderValidationEvents(orderValidation: OrderValidation) {
        when (orderValidation.status) {
            OrderValidationStatus.VALID -> {
                val order = ordersRepository.updateOrder(orderValidation.orderId, OrderStatus.VALID).awaitSuspending()
                val createOutboxEvent = order.toCreateOutboxEvent(objectMapper)
                outboxEventsRepository.createEvent(createOutboxEvent).awaitSuspending()
            }
            OrderValidationStatus.INVALID -> {
                // TODO: Do we need to inform the user here about the out of stock ?
                ordersRepository.updateOrder(orderValidation.orderId, OrderStatus.OUT_OF_STOCK).awaitSuspending()
            }
        }
    }
}