package com.arconsis.data.outboxevents

import com.arconsis.data.Json
import com.arconsis.data.PostgreSQLEnumType
import com.arconsis.domain.outboxevents.AggregateType
import com.arconsis.domain.outboxevents.CreateOutboxEvent
import com.arconsis.domain.outboxevents.OutboxEvent
import io.vertx.core.json.JsonObject
import org.hibernate.annotations.*
import java.time.Instant
import java.util.*
import javax.persistence.*
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "outbox_events")
@TypeDefs(
	TypeDef(
		name = "pgsql_enum",
		typeClass = PostgreSQLEnumType::class
	),
	TypeDef(
		name = "json",
		typeClass = Json::class
	),
)
class OutboxEventEntity(
    @Id
	@GeneratedValue
	var id: UUID? = null,

    @Enumerated(EnumType.STRING)
	@Column(name = "aggregate_type", columnDefinition = "aggregate_type", nullable = false)
	@Type(type = "pgsql_enum")
	var aggregateType: AggregateType,

    @Column(name = "aggregate_id", columnDefinition = "aggregate_id", nullable = false)
	var aggregateId: UUID,

    @Type(type="json")
	@Column(name = "payload", columnDefinition = "payload", nullable = false)
	val payload: JsonObject,

    @CreationTimestamp
	@Column(name = "created_at")
	var createdAt: Instant? = null,

    @UpdateTimestamp
	@Column(name = "updated_at")
	var updatedAt: Instant? = null,
)

fun CreateOutboxEvent.toOutboxEventEntity() = OutboxEventEntity(
	aggregateId = aggregateId,
	aggregateType = aggregateType,
	payload = payload
)

fun OutboxEventEntity.toOutboxEvent() = OutboxEvent(
	id = id!!,
	aggregateId = aggregateId,
	aggregateType = aggregateType,
	payload = payload
)