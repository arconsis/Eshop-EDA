package com.arconsis.data.outboxevents

import com.arconsis.domain.outboxevents.AggregateType
import com.arconsis.domain.outboxevents.CreateOutboxEvent
import com.arconsis.domain.outboxevents.OutboxEvent
import com.arconsis.domain.outboxevents.OutboxEventType
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "orders_outbox_events")
class OutboxEventEntity(
    @Id
    @GeneratedValue
    // unique id of each message; can be used by consumers to detect any duplicate events
    var id: UUID? = null,

    @Column(name = "aggregate_type", columnDefinition = "aggregate_type", nullable = false)
    // the type of the aggregate root to which a given event is related
    var aggregateType: String,

    @Column(name = "aggregate_id", columnDefinition = "aggregate_id", nullable = false)
    // this is the ID of the aggregate object affected by the update operation
    var aggregateId: String,

    // a JSON String representation of the actual event content
    @Column(name = "payload", columnDefinition = "payload", nullable = false)
    val payload: String,

    // type of the event. For example, “OrderCreated.”
    @Column(name = "type", columnDefinition = "type", nullable = false)
    val type: String,

    @CreationTimestamp
    @Column(name = "created_at")
    var createdAt: Instant? = null,

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: Instant? = null,
)

fun CreateOutboxEvent.toOutboxEventEntity() = OutboxEventEntity(
    aggregateId = aggregateId.toString(),
    aggregateType = aggregateType.name,
    type = type.toString(),
    payload = payload
)

fun OutboxEventEntity.toOutboxEvent() = OutboxEvent(
    id = id!!,
    aggregateId = UUID.fromString(aggregateId),
    aggregateType = AggregateType.valueOf(aggregateType),
    type = OutboxEventType.valueOf(type),
    payload = payload
)