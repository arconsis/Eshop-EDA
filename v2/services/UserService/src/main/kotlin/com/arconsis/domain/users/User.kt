package com.arconsis.domain.users

import com.arconsis.domain.outboxevents.AggregateType
import com.arconsis.domain.outboxevents.CreateOutboxEvent
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.vertx.core.json.JsonObject
import java.util.*

data class User(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val email: String,
    val username: String,
)

private fun User.toJsonObject() = JsonObject()
    .put("id", id.toString())
    .put("firstName", firstName)
    .put("email", email)
    .put("username", username)

fun User.toCreateOutboxEvent(): CreateOutboxEvent = CreateOutboxEvent(
    aggregateType = AggregateType.USER,
    aggregateId = this.id,
    payload = toJsonObject()
)