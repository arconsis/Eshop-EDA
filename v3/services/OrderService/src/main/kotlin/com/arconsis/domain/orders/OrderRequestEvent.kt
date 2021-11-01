package com.arconsis.domain.orders

import java.util.*

data class OrderRequestEvent(
  val key: String,
  val value: Order
)

fun Order.toOrderRequestEvent() = OrderRequestEvent(
  key = UUID.randomUUID().toString(),
  value = this
)