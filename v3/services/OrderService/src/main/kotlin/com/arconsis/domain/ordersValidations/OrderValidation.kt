package com.arconsis.domain.ordersValidations

import java.util.*

data class OrderValidation(
  val type: OrderValidationType,
  val productId: String,
  val quantity: Int,
  val orderNo: UUID
)

enum class OrderValidationType {
  VALID,
  INVALID,
}
