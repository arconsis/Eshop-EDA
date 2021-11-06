package com.arconsis.domain.payments

import io.smallrye.reactive.messaging.kafka.Record
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
	val currency: String,
	val status: PaymentStatus,
)

fun Payment.toPaymentRecord(): Record<String, Payment> = Record.of(
	userId.toString(),
	this
)