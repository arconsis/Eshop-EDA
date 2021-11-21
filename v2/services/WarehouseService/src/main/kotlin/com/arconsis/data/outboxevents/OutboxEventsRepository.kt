package com.arconsis.data.outboxevents

import com.arconsis.domain.outboxevents.CreateOutboxEvent
import com.arconsis.domain.outboxevents.OutboxEvent
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OutboxEventsRepository(private val sessionFactory: Mutiny.SessionFactory) {
    fun createEvent(createOutboxEvent: CreateOutboxEvent): Uni<OutboxEvent> {
        val outboxEventEntity = createOutboxEvent.toOutboxEventEntity()

        return sessionFactory.withTransaction { s, _ ->
            s.persist(outboxEventEntity)
                .map { outboxEventEntity.toOutboxEvent() }
        }
    }
}