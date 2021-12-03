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
    ORDER,
    ORDER_VALIDATION,
    SHIPMENT,
    PAYMENT,
}

enum class OutboxEventType {
    // order
    ORDER_REQUESTED,
    ORDER_VALIDATED,
    ORDER_OUT_OF_STOCK,
    ORDER_PAID,
    ORDER_SHIPPED,
    ORDER_COMPLETED,
    ORDER_PAYMENT_FAILED,
    ORDER_CANCELLED,
    ORDER_REFUNDED,
    // order-validation
    ORDER_INVALID,
    // payments
    PAYMENT_SUCCEED,
    PAYMENT_FAILED,
    // shipment
    SHIPMENT_PREPARING_SHIPMENT,
    SHIPMENT_SHIPPED,
    SHIPMENT_DELIVERED,
    SHIPMENT_CANCELLED,
}