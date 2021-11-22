package com.arconsis.domain.outboxevents

import com.fasterxml.jackson.databind.JsonNode
import io.vertx.core.json.JsonObject
import java.util.*

data class CreateOutboxEvent(
    val aggregateType: AggregateType,
    val aggregateId: UUID,
    val payload: JsonObject,
)

data class OutboxEvent(
    val id: UUID,
    val aggregateType: AggregateType,
    val aggregateId: UUID,
    val payload: JsonNode,
)

enum class AggregateType {
    ORDER,
}
