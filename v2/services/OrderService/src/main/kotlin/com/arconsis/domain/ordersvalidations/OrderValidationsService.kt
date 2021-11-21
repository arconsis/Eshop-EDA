package com.arconsis.domain.ordersvalidations

import com.arconsis.data.orders.OrdersRepository
import com.arconsis.data.outboxevents.OutboxEventsRepository
import com.arconsis.domain.orders.OrderStatus
import com.arconsis.domain.orders.toCreateOutboxEvent
import com.fasterxml.jackson.databind.ObjectMapper
import io.smallrye.mutiny.Uni
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class OrderValidationsService(
    private val ordersRepository: OrdersRepository,
    private val outboxEventsRepository: OutboxEventsRepository,
    private val objectMapper: ObjectMapper,
) {

    @Transactional
    fun handleOrderValidationEvents(orderValidation: OrderValidation): Uni<Void> {
        return when (orderValidation.status) {
            OrderValidationStatus.VALID -> handleValidOrderValidation(orderValidation)
            OrderValidationStatus.INVALID -> handleValidOrderInvalidation(orderValidation)
        }
    }

    private fun handleValidOrderValidation(orderValidation: OrderValidation): Uni<Void> {
        return ordersRepository.updateOrder(orderValidation.orderId, OrderStatus.VALID)
            .flatMap { order ->
                val createOutboxEvent = order.toCreateOutboxEvent(objectMapper)
                outboxEventsRepository.createEvent(createOutboxEvent)
            }
            .map {
                null
            }
    }

    private fun handleValidOrderInvalidation(orderValidation: OrderValidation): Uni<Void> =
        ordersRepository.updateOrder(orderValidation.orderId, OrderStatus.OUT_OF_STOCK).replaceWithVoid()
}