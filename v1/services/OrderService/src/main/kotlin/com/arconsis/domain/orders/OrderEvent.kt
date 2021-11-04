package com.arconsis.domain.orders

import com.arconsis.common.Message

enum class OrderEventType {
    ORDER_REQUESTED,
    ORDER_CREATED,
    ORDER_CONFIRMED,
}

data class OrderEvent(
    val key: String,
    val value: Message<OrderEventType, Order>,
)

fun Order.toOrderEvent(type: OrderEventType) = OrderEvent(
    key = userId.toString(),
    value = Message(type, this)
)
