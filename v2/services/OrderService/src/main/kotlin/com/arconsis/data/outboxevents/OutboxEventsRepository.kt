package com.arconsis.data.outboxevents

import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.toOutboxEventEntityEvent
import com.arconsis.domain.outboxevents.OutboxEvent
import com.fasterxml.jackson.databind.JsonNode
import io.debezium.outbox.quarkus.ExportedEvent
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Event

@ApplicationScoped
class OutboxEventsRepository(private val event: Event<ExportedEvent<String, JsonNode>>) {

    fun createEvent(order: Order, session: Mutiny.Session): Uni<OutboxEvent> {
        return Uni.createFrom()
            .completionStage(
                event.fireAsync(order.toOutboxEventEntityEvent())
            ).map {
                it.toOutboxEvent()
            }
    }
}