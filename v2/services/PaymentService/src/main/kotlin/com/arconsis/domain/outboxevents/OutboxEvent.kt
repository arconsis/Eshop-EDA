package com.arconsis.domain.outboxevents

import com.fasterxml.jackson.databind.JsonNode
import java.util.*

data class CreateOutboxEvent(
    val aggregateType: AggregateType,
    val aggregateId: UUID,
    val payload: JsonNode,
)

data class OutboxEvent(
    val id: UUID,
    val aggregateType: AggregateType,
    val aggregateId: UUID,
    val payload: JsonNode,
)

enum class AggregateType {
    PAYMENT,
}
