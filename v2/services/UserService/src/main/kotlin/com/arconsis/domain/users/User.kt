package com.arconsis.domain.users

import com.arconsis.domain.outboxevents.AggregateType
import com.arconsis.domain.outboxevents.CreateOutboxEvent
import com.arconsis.domain.outboxevents.OutboxEventType
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.*

data class User(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val email: String,
    val username: String,
)

fun User.toCreateOutboxEvent(objectMapper: ObjectMapper): CreateOutboxEvent = CreateOutboxEvent(
    aggregateType = AggregateType.USER,
    aggregateId = this.id,
    type = OutboxEventType.USER_CREATED.name,
    payload = objectMapper.writeValueAsString(this)
)