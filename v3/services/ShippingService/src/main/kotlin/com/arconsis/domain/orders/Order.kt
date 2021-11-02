package com.arconsis.domain.orders

import java.util.*

data class Order(
  val userId: UUID,
  val orderId: UUID,
  val amount: String,
  val currency: String,
  val productId: String,
  val quantity: Int,
  val status: OrderStatus,
)

enum class OrderStatus {
  PENDING,
  VALID,
  OUT_OF_STOCK,
  PAID,
  OUT_FOR_SHIPMENT,
  COMPLETED,
  PAYMENT_FAILED,
  CANCELLED,
  REFUNDED
}

val Order.isPaid
  get() = status == OrderStatus.PAID