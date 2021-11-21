package com.arconsis.domain.orders

import com.arconsis.data.orders.OrdersRepository
import com.arconsis.data.outboxevents.OutboxEventsRepository
import com.fasterxml.jackson.databind.ObjectMapper
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.coroutines.awaitSuspending
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
    suspend fun createOrder(createOrder: CreateOrder): Order {
        val order =  ordersRepository.createOrder(createOrder).awaitSuspending()
        val createOutboxEvent = order.toCreateOutboxEvent(objectMapper)
        outboxEventsRepository.createEvent(createOutboxEvent).awaitSuspending()
        return order
    }

    suspend fun getOrder(orderId: UUID): Order {
        return ordersRepository.getOrder(orderId).awaitSuspending()
    }
}