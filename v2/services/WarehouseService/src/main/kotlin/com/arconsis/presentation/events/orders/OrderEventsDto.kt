package com.arconsis.presentation.events.orders

import com.arconsis.domain.outboxevents.AggregateType
import com.arconsis.domain.outboxevents.OutboxEvent
import com.arconsis.domain.outboxevents.OutboxEventType
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer
import java.time.Instant
import java.util.*

data class OrderEventsDto(
    val schema: Schema? = null,
    val payload: Payload
)

data class Schema(
    val type: String? = null,
    val fields: List<SchemaField>? = null,
    val optional: Boolean? = null,
    val name: String? = null
)

data class SchemaField(
    val type: String? = null,
    val fields: List<FieldField>? = null,
    val optional: Boolean? = null,
    val name: String? = null,
    val field: String? = null
)

data class FieldField(
    val type: String? = null,
    val optional: Boolean? = null,
    val name: String? = null,
    val version: Long? = null,
    val field: String? = null,
    val parameters: Parameters? = null,
    val default: String? = null
)

data class Parameters(
    val allowed: String? = null
)


data class Payload(
    val before: OrderEventDtoPayload? = null,
    val after: OrderEventDtoPayload,
    val source: Source,
    val op: String? = null,
    val tsMS: Long? = null,
    val transaction: Any? = null
)

data class Source(
    val version: String? = null,
    val connector: String? = null,
    val name: String? = null,
    val tsMS: Long? = null,
    val snapshot: String? = null,
    val db: String? = null,
    val sequence: String? = null,
    val schema: String? = null,
    val table: String? = null,
    val txID: Long? = null,
    val lsn: Long? = null,
    val xmin: Any? = null
)

data class OrderEventDtoPayload(
    var id: String,
    var aggregate_type: String,
    var aggregate_id: String,
    val payload: String,
    val type: String,
    var created_at: Instant? = null,
    var updated_at: Instant? = null,
)

fun OrderEventDtoPayload.toOutboxEvent() = OutboxEvent(
    id = UUID.fromString(id),
    aggregateId = UUID.fromString(aggregate_id),
    aggregateType = AggregateType.valueOf(aggregate_type),
    type = OutboxEventType.valueOf(type),
    payload = payload
)

class OrderEventsDtoDeserializer : ObjectMapperDeserializer<OrderEventsDto>(OrderEventsDto::class.java)
