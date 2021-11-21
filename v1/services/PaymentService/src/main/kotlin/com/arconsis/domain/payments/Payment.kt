package com.arconsis.domain.payments

import io.smallrye.reactive.messaging.kafka.Record
import java.util.*

data class Payment(
    val transactionId: UUID?,
    val orderId: UUID,
    val userId: UUID,
    val amount: Double,
    val currency: String,
    val status: PaymentStatus,
)

enum class PaymentStatus {
    SUCCESS,
    FAILED,
    REFUNDED,
}

data class CreatePayment(
    val orderId: UUID,
    val userId: UUID,
    val amount: Double,
    val currency: String,
)

fun CreatePayment.toPayment(transactionId: UUID, status: PaymentStatus) = Payment(
    transactionId = transactionId,
    orderId = orderId,
    userId = userId,
    amount = amount,
    currency = currency,
    status = status,
)

fun Payment.toPaymentRecord(): Record<String, Payment> = Record.of(
    orderId.toString(),
    this
)