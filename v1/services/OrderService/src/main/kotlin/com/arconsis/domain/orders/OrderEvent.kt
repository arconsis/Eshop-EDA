package com.arconsis.domain.orders

import com.arconsis.common.Message
import io.smallrye.reactive.messaging.kafka.Record

enum class OrderEventType {
    ORDER_REQUESTED,
    ORDER_CREATED,
    ORDER_CONFIRMED,
}

class OrderMessage(override val type: OrderEventType, override val payload: Order) : Message<OrderEventType, Order>

fun Order.toOrderRecord(type: OrderEventType): Record<String, OrderMessage> = Record.of(
    userId.toString(),
    OrderMessage(type, this)
)