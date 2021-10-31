package com.arconsis.domain.inventory

import com.arconsis.domain.orders.Order
import java.util.*

data class OrderValidationEvent(
  val key: String,
  val value: OrderValidationDto
)

data class OrderValidationDto(
  val type: String,
  val productId: String,
  val quantity: Int,
  val orderNo: UUID
)

enum class OrderValidationType(val type: String) {
  ORDER_VALIDATED("OrderValidated"),
  ORDER_INVALID("OrderInvalid"),
}

fun Order.toOrderValidationEvent(type: OrderValidationType) = OrderValidationEvent(
  key = UUID.randomUUID().toString(),
  value = OrderValidationDto(
    type = type.type,
    productId = productId,
    quantity = quantity,
    orderNo = orderNo
  )
)