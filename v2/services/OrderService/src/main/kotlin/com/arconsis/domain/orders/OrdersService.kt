package com.arconsis.domain.orders

import com.arconsis.data.orders.OrdersRepository
import com.arconsis.data.outboxevents.OutboxEventsRepository
import com.fasterxml.jackson.databind.ObjectMapper
import io.smallrye.mutiny.Uni
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class OrdersService(
    private val ordersRepository: OrdersRepository,
    private val outboxEventsRepository: OutboxEventsRepository,
    private val objectMapper: ObjectMapper,
) {
    @Transactional
    fun createOrder(createOrder: CreateOrder): Uni<Order> {
        return ordersRepository.createOrder(createOrder)
            .flatMap { order ->
                val createOutboxEvent = order.toCreateOutboxEvent(objectMapper)
                outboxEventsRepository.createEvent(createOutboxEvent).map {
                    order
                }
            }
    }

    fun getOrder(orderId: UUID): Uni<Order> {
        return ordersRepository.getOrder(orderId)
    }
}