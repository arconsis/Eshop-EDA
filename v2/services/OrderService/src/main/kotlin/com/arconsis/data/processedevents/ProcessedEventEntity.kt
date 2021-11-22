package com.arconsis.data.processedevents

import com.arconsis.domain.processedevents.ProcessedEvent
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table


@Entity
@Table(name = "processed_events")
class ProcessedEventEntity(
    @Column(nullable = false, name = "event_id")
    @Id
    var eventId: UUID,

    @Column(nullable = false, name = "processed_at")
    var processedAt: Instant,

    @CreationTimestamp
    @Column(name = "created_at")
    var createdAt: Instant? = null,

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: Instant? = null
)

fun ProcessedEventEntity.toProcessedEvent() = ProcessedEvent(
    eventId = eventId,
    processedAt = processedAt,
)

fun ProcessedEvent.toProcessedEventEntity() = ProcessedEventEntity(
    eventId = eventId,
    processedAt = processedAt,
)