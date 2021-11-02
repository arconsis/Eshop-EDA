package com.arconsis.domain.orders

import java.util.*

data class OrderValidationEvent(
    val key: String,
    val value: OrderValidation,
)

data class OrderValidation(
  val type: OrderValidationType,
  val productId: String,
  val quantity: Int,
  val orderId: UUID,
)

enum class OrderValidationType {
    VALID,
    INVALID,
}

fun Order.toOrderValidationEvent(type: OrderValidationType) = OrderValidationEvent(
    key = orderId.toString(),
    value = OrderValidation(
        type = type,
        productId = productId,
        quantity = quantity,
        orderId = orderId
    )
)