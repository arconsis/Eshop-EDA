package com.arconsis.domain.outboxevents

import com.arconsis.domain.payments.PaymentStatus
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
    PAYMENT,
}

typealias OutboxEventType = PaymentStatus