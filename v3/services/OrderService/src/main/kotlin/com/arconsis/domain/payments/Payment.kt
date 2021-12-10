package com.arconsis.domain.payments

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
    SUCCEED,
    FAILED,
    REFUNDED
}

val Payment.isSuccess
    get() = status == PaymentStatus.SUCCEED