package com.arconsis.data.processedevents

import com.arconsis.domain.processedevents.ProcessedEvent
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ProcessedEventsRepository {
    fun createEvent(event: ProcessedEvent, session: Mutiny.Session): Uni<ProcessedEvent> {
        val eventEntity = event.toProcessedEventEntity()
        return session.persist(event.toProcessedEventEntity())
            .map {
                eventEntity.toProcessedEvent()
            }
    }

    fun getEvent(eventId: UUID, session: Mutiny.Session): Uni<ProcessedEvent?> {
        return session.find(ProcessedEventEntity::class.java, eventId)
            .map { eventEntity ->
                eventEntity?.toProcessedEvent()
            }
    }
}