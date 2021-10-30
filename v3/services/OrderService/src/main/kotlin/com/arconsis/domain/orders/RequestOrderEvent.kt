package com.arconsis.domain.orders

import java.util.*

private data class RequestOrderEvent(
  val key: UUID,
  val value: Order
)

fun createRequestOrderEvent(
  order: Order,
): Pair<String, Order> {
  val event = RequestOrderEvent(
    key = UUID.randomUUID(),
    value = order
  )
  return Pair(event.key.toString(), event.value)
}