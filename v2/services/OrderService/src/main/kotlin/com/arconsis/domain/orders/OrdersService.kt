package com.arconsis.domain.orders

import com.arconsis.data.orders.OrdersRepository
import com.arconsis.data.outboxevents.OutboxEventsRepository
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrdersService(
    private val ordersRepository: OrdersRepository,
    private val outboxEventsRepository: OutboxEventsRepository,
    private val sessionFactory: Mutiny.SessionFactory
) {
    fun createOrder(createOrder: CreateOrder): Uni<Order> {
        return sessionFactory.withTransaction { session, _ ->
            ordersRepository.createOrder(createOrder, session)
                .flatMap { order ->
                    val createOutboxEvent = order.toCreateOutboxEvent()
                    outboxEventsRepository.createEvent(createOutboxEvent, session).map {
                        order
                    }
                }
        }
    }

    fun getOrder(orderId: UUID): Uni<Order> {
        return ordersRepository.getOrder(orderId)
    }
}