package com.arconsis.domain.payments

import com.arconsis.data.outboxevents.OutboxEventEntityEvent
import com.arconsis.domain.outboxevents.AggregateType
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

fun CreatePayment.toPayment(transactionId: UUID, status: PaymentStatus) = Payment(
    transactionId = transactionId,
    orderId = orderId,
    userId = userId,
    amount = amount,
    currency = currency,
    status = status,
)

fun Payment.toOutboxEventEntityEvent(): OutboxEventEntityEvent {
    val mapper = ObjectMapper()
    val payload = mapper.createObjectNode()
        .put("transactionId", transactionId.toString())
        .put("orderId", orderId.toString())
        .put("userId", userId.toString())
        .put("amount", amount)
        .put("currency", currency)
        .put("status", status.name)

    return OutboxEventEntityEvent(
        aggregateId = transactionId,
        aggregateType = AggregateType.PAYMENT,
        type = status,
        node = payload
    )
}