package com.arconsis.data.processedevents

import com.arconsis.domain.processedevents.ProcessedEvent
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ProcessedEventsRepository(val sessionFactory: Mutiny.SessionFactory) {
    fun createEvent(event: ProcessedEvent, session: Mutiny.Session? = null): Uni<ProcessedEvent> {
        return session?.createEvent(event) ?: createEvent(event)
    }

    private fun Mutiny.Session.createEvent(event: ProcessedEvent): Uni<ProcessedEvent> {
        val eventEntity = event.toProcessedEventEntity()
        return this.persist(event.toProcessedEventEntity())
            .map {
                eventEntity.toProcessedEvent()
            }
    }

    private fun createEvent(event: ProcessedEvent): Uni<ProcessedEvent> {
        return sessionFactory.withTransaction { session ->
            val eventEntity = event.toProcessedEventEntity()
            session.persist(event.toProcessedEventEntity())
                .map {
                    eventEntity.toProcessedEvent()
                }
        }
    }

    fun getEvent(eventId: UUID, session: Mutiny.Session): Uni<ProcessedEvent?> {
        return session.find(ProcessedEventEntity::class.java, eventId)
            .map { eventEntity ->
                eventEntity?.toProcessedEvent()
            }
    }
}