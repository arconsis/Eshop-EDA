package com.arconsis.presentation.events.common

import com.arconsis.domain.outboxevents.AggregateType
import com.arconsis.domain.outboxevents.OutboxEvent
import com.arconsis.domain.outboxevents.OutboxEventType
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer
import java.time.Instant
import java.util.*

data class WarehouseEventsDto(
    val schema: Schema? = null,
    val payload: WarehouseEventPayload
)

data class WarehouseEventPayload(
    val before: WarehouseEventDtoPayload? = null,
    val after: WarehouseEventDtoPayload,
    val source: Source,
    val op: String? = null,
    val tsMS: Long? = null,
    val transaction: Any? = null
)

data class WarehouseEventDtoPayload(
    var id: String,
    var aggregate_type: String,
    var aggregate_id: String,
    val payload: String,
    val type: String,
    var created_at: Instant? = null,
    var updated_at: Instant? = null,
)

fun WarehouseEventDtoPayload.toOutboxEvent() = OutboxEvent(
    id = UUID.fromString(id),
    aggregateId = UUID.fromString(aggregate_id),
    aggregateType = AggregateType.valueOf(aggregate_type),
    type = OutboxEventType.valueOf(type),
    payload = payload
)

class WarehouseEventsDtoDeserializer : ObjectMapperDeserializer<WarehouseEventsDto>(WarehouseEventsDto::class.java)
