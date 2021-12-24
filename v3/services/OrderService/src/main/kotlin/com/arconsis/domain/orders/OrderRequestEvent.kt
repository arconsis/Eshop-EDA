package com.arconsis.domain.orders

data class OrderRequestEvent(
    val key: String,
    val value: Order,
)

fun Order.toOrderRequestEvent() = OrderRequestEvent(
    key = orderId.toString(),
    value = this
)