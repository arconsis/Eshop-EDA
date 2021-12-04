package com.arconsis.presentation.events.payments

import com.arconsis.domain.outboxevents.AggregateType
import com.arconsis.domain.outboxevents.OutboxEvent
import com.arconsis.domain.outboxevents.OutboxEventType
import com.fasterxml.jackson.annotation.JsonProperty
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer
import java.util.*

data class PaymentEventDto(
    @JsonProperty("payload") val payload: PaymentEventDtoPayload
)

data class PaymentEventDtoPayload(
    @JsonProperty("before") val previousValue: PaymentEventDtoValue? = null,
    @JsonProperty("after") val currentValue: PaymentEventDtoValue,
)

data class PaymentEventDtoValue(
    @JsonProperty("id") var id: String,
    @JsonProperty("aggregate_type") var aggregateType: String,
    @JsonProperty("aggregate_id") var aggregateId: String,
    @JsonProperty("payload") val payload: String,
    @JsonProperty("type") val type: String,
)

fun PaymentEventDtoValue.toOutboxEvent() = OutboxEvent(
    id = UUID.fromString(id),
    aggregateId = UUID.fromString(aggregateId),
    aggregateType = AggregateType.valueOf(aggregateType),
    type = OutboxEventType.valueOf(type),
    payload = payload
)

class PaymentEventsDtoDeserializer : ObjectMapperDeserializer<PaymentEventDto>(PaymentEventDto::class.java)