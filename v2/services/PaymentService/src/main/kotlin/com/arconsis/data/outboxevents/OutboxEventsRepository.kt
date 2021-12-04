package com.arconsis.data.outboxevents

import com.arconsis.domain.outboxevents.CreateOutboxEvent
import com.arconsis.domain.outboxevents.OutboxEvent
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OutboxEventsRepository {
    fun createEvent(createOutboxEvent: CreateOutboxEvent, session: Mutiny.Session): Uni<OutboxEvent> {
        val outboxEventEntity = createOutboxEvent.toOutboxEventEntity()
        return session.persist(outboxEventEntity)
            .map {
                outboxEventEntity.toOutboxEvent()
            }

    }
}