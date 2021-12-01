package com.arconsis.domain.ordersvalidations

import com.arconsis.data.orders.OrdersRepository
import com.arconsis.data.outboxevents.OutboxEventsRepository
import com.arconsis.domain.orders.OrderStatus
import com.arconsis.domain.orders.toCreateOutboxEvent
import com.fasterxml.jackson.databind.ObjectMapper
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrderValidationsService(
    private val ordersRepository: OrdersRepository,
    private val outboxEventsRepository: OutboxEventsRepository,
    private val sessionFactory: Mutiny.SessionFactory,
    private val objectMapper: ObjectMapper
) {

    fun handleOrderValidationEvents(orderValidation: OrderValidation): Uni<Void> {
        return when (orderValidation.status) {
            OrderValidationStatus.VALIDATED -> handleValidOrderValidation(orderValidation)
            OrderValidationStatus.INVALID -> handleValidOrderInvalidation(orderValidation)
        }
    }

    private fun handleValidOrderValidation(orderValidation: OrderValidation): Uni<Void> {
        return sessionFactory.withTransaction { session, _ ->
            ordersRepository.updateOrder(orderValidation.orderId, OrderStatus.VALIDATED, session)
                .flatMap { order ->
                    val createOutboxEvent = order.toCreateOutboxEvent(objectMapper)
                    outboxEventsRepository.createEvent(createOutboxEvent, session)
                }
                .map {
                    null
                }
        }
    }

    private fun handleValidOrderInvalidation(orderValidation: OrderValidation): Uni<Void> =
        ordersRepository.updateOrder(orderValidation.orderId, OrderStatus.OUT_OF_STOCK, null).replaceWithVoid()
}