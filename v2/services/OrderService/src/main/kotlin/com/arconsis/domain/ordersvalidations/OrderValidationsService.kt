package com.arconsis.domain.ordersvalidations

import com.arconsis.data.orders.OrdersRepository
import com.arconsis.data.outboxevents.OutboxEventsRepository
import com.arconsis.domain.orders.OrderStatus
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrderValidationsService(
    private val ordersRepository: OrdersRepository,
    private val outboxEventsRepository: OutboxEventsRepository,
    private val sessionFactory: Mutiny.SessionFactory,
) {
    fun handleOrderValidationEvents(orderValidation: OrderValidation): Uni<Void> {
        return when (orderValidation.status) {
            OrderValidationStatus.VALID -> handleValidOrderValidation(orderValidation)
            OrderValidationStatus.INVALID -> handleValidOrderInvalidation(orderValidation)
        }
    }

    private fun handleValidOrderValidation(orderValidation: OrderValidation): Uni<Void> {
        return sessionFactory.withTransaction { session, _ ->
            ordersRepository.updateOrder(orderValidation.orderId, OrderStatus.VALID, session)
                .flatMap { order ->
                    outboxEventsRepository.createEvent(order, session)
                }
                .map {
                    null
                }
        }
    }

    private fun handleValidOrderInvalidation(orderValidation: OrderValidation): Uni<Void> =
        ordersRepository.updateOrder(orderValidation.orderId, OrderStatus.OUT_OF_STOCK, null).replaceWithVoid()
}