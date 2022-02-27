package com.arconsis.domain.outboxevents

import java.util.*

data class OutboxEvent(
    val id: UUID,
    val aggregateType: AggregateType,
    val aggregateId: UUID,
    val type: OutboxEventType,
    val payload: String,
)

enum class AggregateType {
    USER,
}

enum class OutboxEventType {
    // users
    USER_CREATED
}