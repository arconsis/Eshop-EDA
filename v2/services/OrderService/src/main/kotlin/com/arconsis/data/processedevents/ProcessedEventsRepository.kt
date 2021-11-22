package com.arconsis.data.processedevents

import com.arconsis.domain.processedevents.ProcessedEvent
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ProcessedEventsRepository(private val sessionFactory: Mutiny.SessionFactory) {
    fun createEvent(event: ProcessedEvent): Uni<ProcessedEvent> {
        val eventEntity = event.toProcessedEventEntity()
        return sessionFactory.withTransaction { s, _ ->
            s.persist(event.toProcessedEventEntity())
                .map { eventEntity.toProcessedEvent() }
        }
    }

    fun getEvent(eventId: UUID): Uni<ProcessedEvent> {
        return sessionFactory.withTransaction { s, _ ->
            s.find(ProcessedEventEntity::class.java, eventId)
                .map { eventEntity -> eventEntity.toProcessedEvent() }
        }
    }
}