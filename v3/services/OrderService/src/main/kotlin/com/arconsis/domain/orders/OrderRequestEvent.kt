package com.arconsis.domain.orders

import java.util.*

private data class OrderRequestEvent(
  val key: UUID,
  val value: Order
)

fun creatOrderRequestEventPair(
  order: Order,
): Pair<String, Order> {
  val event = OrderRequestEvent(
    key = UUID.randomUUID(),
    value = order
  )
  return Pair(event.key.toString(), event.value)
}