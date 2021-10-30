package com.arconsis.domain.orders

import java.util.*

data class Order(
  val userId: UUID,
  val orderNo: UUID,
  val amount: String,
  val currency: String,
  val productId: String,
  val quantity: Int,
  val status: OrderStatus
)

enum class OrderStatus {
  pending,
  valid,
  out_of_stock,
  paid,
  out_for_shipment,
  completed,
  payment_failed,
  cancelled,
  refunded
}
