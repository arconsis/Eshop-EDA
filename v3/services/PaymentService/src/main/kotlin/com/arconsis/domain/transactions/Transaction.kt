package com.arconsis.domain.transactions

import java.util.*

data class Transaction(
  val transactionId: UUID,
  val orderId: UUID,
  val userId: UUID,
  val amount: String,
  val currency: String,
  val status: TransactionStatus,
)

enum class TransactionStatus {
  SUCCESS,
  FAILED,
}