package com.arconsis.domain.users

import com.arconsis.data.outboxevents.OutboxEventEntityEvent
import com.arconsis.domain.outboxevents.AggregateType
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.*

data class User(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val email: String,
    val username: String,
)

fun User.toOutboxEventEntityEvent(): OutboxEventEntityEvent {
    val mapper = ObjectMapper()
    val payload = mapper.createObjectNode()
        .put("id", id.toString())
        .put("firstName", firstName)
        .put("email", email)
        .put("username", username)
    return OutboxEventEntityEvent(
        aggregateId = id,
        aggregateType = AggregateType.USER,
        type = "USER_CREATED",
        node = payload
    )
}