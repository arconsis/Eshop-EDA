package com.arconsis.domain.outboxevents

import java.util.*

data class CreateOutboxEvent(
    val aggregateType: AggregateType,
    val aggregateId: UUID,
    val payload: String,
    val type: String
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

// TODO: merge ShipmentStatus and OrderValidationStatus
enum class OutboxEventType {
    VALIDATED,
    INVALID,
    PREPARING_SHIPMENT,
    SHIPPED,
    DELIVERED,
    CANCELLED,
}