package com.arconsis.domain.transactions

import com.arconsis.domain.orders.Order
import java.util.*

data class TransactionEvent(
  val key: String,
  val value: Transaction,
)

fun Order.toTransactionEvent(status: TransactionStatus) = TransactionEvent(
  key = orderId.toString(),
  value = Transaction(
    transactionId = UUID.randomUUID(),
    orderId = this.orderId,
    userId = this.userId,
    amount = this.amount,
    currency = this.currency,
    status = status,
  )
)