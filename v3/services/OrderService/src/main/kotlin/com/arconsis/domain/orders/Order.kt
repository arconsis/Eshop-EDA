package com.arconsis.domain.orders

import com.arconsis.presentation.orders.dto.OrderCreateDto
import java.util.*

data class Order(
  val userId: UUID,
  val orderId: UUID,
  val amount: String,
  val currency: String,
  val productId: String,
  val quantity: Int,
  val status: OrderStatus
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

fun OrderCreateDto.toPendingOrder() = Order(
  userId = userId,
  orderId = UUID.randomUUID(),
  amount = amount,
  currency = currency,
  productId = productId,
  quantity = quantity,
  status = OrderStatus.PENDING
)
