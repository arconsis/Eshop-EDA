package com.arconsis.data.outboxevents

import com.arconsis.domain.outboxevents.CreateOutboxEvent
import com.arconsis.domain.outboxevents.OutboxEvent
import javax.enterprise.context.ApplicationScoped
import javax.persistence.EntityManager

@ApplicationScoped
class OutboxEventsRepository(private val entityManager: EntityManager) {

    fun createEvent(createOutboxEvent: CreateOutboxEvent): OutboxEvent {
        val outboxEventEntity = createOutboxEvent.toOutboxEventEntity()
        entityManager.persist(outboxEventEntity)
        entityManager.flush()

        return outboxEventEntity.toOutboxEvent()
    }
}