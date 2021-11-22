package com.arconsis.data.outboxevents

import com.arconsis.domain.outboxevents.OutboxEvent
import com.arconsis.domain.payments.Payment
import com.arconsis.domain.payments.toOutboxEventEntityEvent
import com.fasterxml.jackson.databind.JsonNode
import io.debezium.outbox.quarkus.ExportedEvent
import io.smallrye.mutiny.Uni
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Event

@ApplicationScoped
class OutboxEventsRepository(private val event: Event<ExportedEvent<String, JsonNode>>) {
    fun createEvent(payment: Payment): Uni<OutboxEvent> {
        return Uni.createFrom()
            .completionStage(
                event.fireAsync(payment.toOutboxEventEntityEvent())
            ).map {
                it.toOutboxEvent()
            }
    }
}