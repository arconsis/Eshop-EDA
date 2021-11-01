package com.arconsis.domain.payments

import com.arconsis.domain.ordersValidations.OrderValidation
import com.arconsis.domain.ordersValidations.OrderValidationType
import java.util.*

data class Payment(
  val transactionId: UUID,
  val orderId: UUID,
  val userId: UUID,
  val amount: String,
  val currency: String,
  val status: PaymentStatus,
)

enum class PaymentStatus {
  SUCCESS,
  FAILED,
}

val Payment.isSuccess
  get() = status == PaymentStatus.SUCCESS