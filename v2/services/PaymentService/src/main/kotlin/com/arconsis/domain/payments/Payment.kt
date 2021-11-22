package com.arconsis.domain.payments

import com.arconsis.domain.outboxevents.AggregateType
import com.arconsis.domain.outboxevents.CreateOutboxEvent
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.*

data class Payment(
    val transactionId: UUID,
    val orderId: UUID,
    val userId: UUID,
    val amount: Double,
    val currency: String,
    val status: PaymentStatus,
)

enum class PaymentStatus {
    SUCCESS,
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
    aggregateId = this.transactionId,
    type = this.status.toString(),
    payload = objectMapper.writeValueAsString(this)
)

fun CreatePayment.toPayment(transactionId: UUID, status: PaymentStatus) = Payment(
    transactionId = transactionId,
    orderId = orderId,
    userId = userId,
    amount = amount,
    currency = currency,
    status = status,
)