package com.arconsis.domain.orders

import com.arconsis.presentation.http.orders.dto.OrderCreateDto
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Channel
import org.eclipse.microprofile.reactive.messaging.Emitter
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrdersService(@Channel("orders-out") private val emitter: Emitter<Record<String, Order>>) {
    suspend fun createOrder(orderCreateDto: OrderCreateDto): Order {
        val pendingOrder = orderCreateDto.toPendingOrder()
        val event = pendingOrder.toOrderRequestEvent()
        sendOrderEvent(event)
        return pendingOrder
    }

    suspend fun sendOrderEvent(event: OrderRequestEvent) {
        emitter.send(Record.of(event.key, event.value)).toCompletableFuture()
    }
}