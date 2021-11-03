package com.arconsis.domain.orders

data class OrderRequestEvent(
	val key: String,
	val value: Order,
)

fun Order.toOrderRequestEvent() = OrderRequestEvent(
	key = userId.toString(),
	value = this
)