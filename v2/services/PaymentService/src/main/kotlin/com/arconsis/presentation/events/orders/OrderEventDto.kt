package com.arconsis.presentation.events.orders

import com.arconsis.domain.outboxevents.AggregateType
import com.arconsis.domain.outboxevents.OutboxEvent
import com.arconsis.domain.outboxevents.OutboxEventType
import com.fasterxml.jackson.annotation.JsonProperty
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer
import java.util.*

data class OrderEventDto(
    @JsonProperty("payload") val payload: OrderEventDtoPayload
)

data class OrderEventDtoPayload(
    @JsonProperty("before") val previousValue: OrderEventDtoValue? = null,
    @JsonProperty("after") val currentValue: OrderEventDtoValue,
)

data class OrderEventDtoValue(
    @JsonProperty("id") var id: String,
    @JsonProperty("aggregate_type") var aggregateType: String,
    @JsonProperty("aggregate_id") var aggregateId: String,
    @JsonProperty("payload") val payload: String,
    @JsonProperty("type") val type: String,
)

fun OrderEventDtoValue.toOutboxEvent() = OutboxEvent(
    id = UUID.fromString(id),
    aggregateId = UUID.fromString(aggregateId),
    aggregateType = AggregateType.valueOf(aggregateType),
    type = OutboxEventType.valueOf(type),
    payload = payload
)

class OrderEventsDtoDeserializer : ObjectMapperDeserializer<OrderEventDto>(OrderEventDto::class.java)