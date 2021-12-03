package com.arconsis.domain.payments

import com.arconsis.domain.outboxevents.AggregateType
import com.arconsis.domain.outboxevents.CreateOutboxEvent
import com.arconsis.domain.outboxevents.OutboxEventType
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.*

data class Payment(
    val id: UUID?,
    val transactionId: UUID?,
    val orderId: UUID,
    val userId: UUID,
    val amount: Double,
    val currency: String,
    val status: PaymentStatus,
)

enum class PaymentStatus {
    SUCCEED,
    FAILED,
}

data class CreatePayment(
    val orderId: UUID,
    val userId: UUID,
    val amount: Double,
    val currency: String
)

fun Payment.toCreateOutboxEvent(objectMapper: ObjectMapper): CreateOutboxEvent = CreateOutboxEvent(
    aggregateType = AggregateType.PAYMENT,
    aggregateId = this.id!!,
    type = this.status.toOutboxEventType(),
    payload = objectMapper.writeValueAsString(this)
)

private fun PaymentStatus.toOutboxEventType(): OutboxEventType = when (this) {
    PaymentStatus.SUCCEED -> OutboxEventType.PAYMENT_SUCCEED
    PaymentStatus.FAILED -> OutboxEventType.PAYMENT_FAILED
}

fun CreatePayment.toPayment(transactionId: UUID, status: PaymentStatus) = Payment(
    id = null,
    transactionId = transactionId,
    orderId = orderId,
    userId = userId,
    amount = amount,
    currency = currency,
    status = status,
)