package com.arconsis.presentation.events.users

import com.arconsis.domain.outboxevents.AggregateType
import com.arconsis.domain.outboxevents.OutboxEvent
import com.arconsis.domain.outboxevents.OutboxEventType
import com.fasterxml.jackson.annotation.JsonProperty
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer
import java.util.*

data class UserEventDto(
    @JsonProperty("payload") val payload: UserEventDtoPayload
)

data class UserEventDtoPayload(
    @JsonProperty("before") val previousValue: UserEventDtoValue? = null,
    @JsonProperty("after") val currentValue: UserEventDtoValue,
)

data class UserEventDtoValue(
    @JsonProperty("id") var id: String,
    @JsonProperty("aggregate_type") var aggregateType: String,
    @JsonProperty("aggregate_id") var aggregateId: String,
    @JsonProperty("payload") val payload: String,
    @JsonProperty("type") val type: String,
)

data class CreateUser(
    @JsonProperty("id") val userId: UUID,
    @JsonProperty("firstName") val firstName: String,
    @JsonProperty("lastName") val lastName: String,
    @JsonProperty("email") val email: String,
    @JsonProperty("username") val username: String,
)

fun UserEventDtoValue.toOutboxEvent() = OutboxEvent(
    id = UUID.fromString(id),
    aggregateId = UUID.fromString(aggregateId),
    aggregateType = AggregateType.valueOf(aggregateType),
    type = OutboxEventType.valueOf(type),
    payload = payload
)

class UserEventsDtoDeserializer : ObjectMapperDeserializer<UserEventDto>(UserEventDto::class.java)