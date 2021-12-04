package com.arconsis.presentation.events.common

import com.arconsis.domain.outboxevents.AggregateType
import com.arconsis.domain.outboxevents.OutboxEvent
import com.arconsis.domain.outboxevents.OutboxEventType
import com.fasterxml.jackson.annotation.JsonProperty
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer
import java.util.*

data class WarehouseEventDto(
    @JsonProperty("payload") val payload: WarehouseEventDtoPayload
)

data class WarehouseEventDtoPayload(
    @JsonProperty("before") val previousValue: WarehouseEventDtoValue? = null,
    @JsonProperty("after") val currentValue: WarehouseEventDtoValue,
)

data class WarehouseEventDtoValue(
    @JsonProperty("id") var id: String,
    @JsonProperty("aggregate_type") var aggregateType: String,
    @JsonProperty("aggregate_id") var aggregateId: String,
    @JsonProperty("payload") val payload: String,
    @JsonProperty("type") val type: String,
)

fun WarehouseEventDtoValue.toOutboxEvent() = OutboxEvent(
    id = UUID.fromString(id),
    aggregateId = UUID.fromString(aggregateId),
    aggregateType = AggregateType.valueOf(aggregateType),
    type = OutboxEventType.valueOf(type),
    payload = payload
)

class WarehouseEventsDtoDeserializer : ObjectMapperDeserializer<WarehouseEventDto>(WarehouseEventDto::class.java)
