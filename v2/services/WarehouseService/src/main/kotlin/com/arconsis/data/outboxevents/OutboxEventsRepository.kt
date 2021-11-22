package com.arconsis.data.outboxevents

import com.arconsis.domain.ordersvalidations.OrderValidation
import com.arconsis.domain.ordersvalidations.toOutboxEventEntityEvent
import com.arconsis.domain.outboxevents.OutboxEvent
import com.arconsis.domain.shipments.Shipment
import com.arconsis.domain.shipments.toOutboxEventEntityEvent
import com.fasterxml.jackson.databind.JsonNode
import io.debezium.outbox.quarkus.ExportedEvent
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Event

@ApplicationScoped
class OutboxEventsRepository(private val event: Event<ExportedEvent<String, JsonNode>>) {
    fun createShipmentEvent(shipment: Shipment, session: Mutiny.Session): Uni<OutboxEvent> {
        return Uni.createFrom()
            .completionStage(
                event.fireAsync(shipment.toOutboxEventEntityEvent())
            ).map {
                it.toOutboxEvent()
            }
    }

    fun createOrderValidationEvent(orderValidation: OrderValidation, session: Mutiny.Session): Uni<OutboxEvent> {
        return Uni.createFrom()
            .completionStage(
                event.fireAsync(orderValidation.toOutboxEventEntityEvent())
            ).map {
                it.toOutboxEvent()
            }
    }
}