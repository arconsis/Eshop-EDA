package com.arconsis.domain.outboxevents

import java.util.*

data class CreateOutboxEvent(
    val aggregateType: AggregateType,
    val aggregateId: UUID,
    val payload: Map<String, Any>,
)

data class OutboxEvent(
    val id: UUID,
    val aggregateType: AggregateType,
    val aggregateId: UUID,
    val payload: Map<String, Any>,
)

enum class AggregateType {
    SHIPMENT,
    ORDER_VALIDATION,
}