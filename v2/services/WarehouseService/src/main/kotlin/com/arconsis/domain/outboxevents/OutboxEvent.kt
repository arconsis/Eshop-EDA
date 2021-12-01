package com.arconsis.domain.outboxevents

import java.util.*

data class CreateOutboxEvent(
    val aggregateType: AggregateType,
    val aggregateId: UUID,
    val payload: String,
    val type: OutboxEventType
)

data class OutboxEvent(
    val id: UUID,
    val aggregateType: AggregateType,
    val aggregateId: UUID,
    val type: OutboxEventType,
    val payload: String,
)

enum class AggregateType {
    SHIPMENT,
    ORDER_VALIDATION,
}

enum class OutboxEventType {
    ORDER_VALIDATED,
    ORDER_INVALID,
    SHIPMENT_PREPARING_SHIPMENT,
    SHIPMENT_SHIPPED,
    SHIPMENT_DELIVERED,
    SHIPMENT_CANCELLED,
}