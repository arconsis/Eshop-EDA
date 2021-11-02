package com.arconsis.domain.payments

import com.arconsis.domain.orders.Order
import java.util.*

data class PaymentEvent(
  val key: String,
  val value: Payment,
)

fun Order.toPaymentEvent(status: PaymentStatus) = PaymentEvent(
  key = orderId.toString(),
  value = Payment(
    transactionId = UUID.randomUUID(),
    orderId = this.orderId,
    userId = this.userId,
    amount = this.amount,
    currency = this.currency,
    status = status,
  )
)