package com.arconsis.domain.events

import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrderRequestEvent
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Channel
import org.eclipse.microprofile.reactive.messaging.Emitter
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class EventsService(@Channel("orders-out") private val emitter: Emitter<Record<String, Order>>) {

    suspend fun sendOrderEvent(event: OrderRequestEvent) {
        emitter.send(Record.of(event.key, event.value)).toCompletableFuture()
    }
}
