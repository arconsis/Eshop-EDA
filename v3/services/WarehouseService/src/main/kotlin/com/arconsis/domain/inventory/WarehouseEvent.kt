package com.arconsis.domain.inventory

import com.arconsis.domain.orders.Order
import java.util.*

data class WarehouseEvent(
  val key: String,
  val value: Inventory
)

data class Inventory(
  val type: String,
  val productId: String,
  val quantity: Int,
  val orderNo: UUID
)

enum class WarehouseEventType(val type: String) {
  ORDER_VALIDATED("OrderValidated"),
  ORDER_INVALID("OrderInvalid"),
}

fun Order.toWarehouseEvent(type: WarehouseEventType) = WarehouseEvent(
  key = UUID.randomUUID().toString(),
  value = Inventory(
    type = type.type,
    productId = productId,
    quantity = quantity,
    orderNo = orderNo
  )
)