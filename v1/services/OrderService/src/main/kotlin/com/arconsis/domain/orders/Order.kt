package com.arconsis.domain.orders

import io.smallrye.reactive.messaging.kafka.Record
import java.util.*

data class CreateOrder(
    val userId: UUID,
    val amount: Double,
    val currency: String,
    val productId: String,
    val quantity: Int,
)

data class Order(
    val id: UUID,
    val userId: UUID,
    val amount: Double,
    val currency: String,
    val productId: String,
    val quantity: Int,
    val status: OrderStatus,
)

enum class OrderStatus {
    REQUESTED,
    VALIDATED,
    OUT_OF_STOCK,
    PAID,
    SHIPPED,
    COMPLETED,
    PAYMENT_FAILED,
    CANCELLED,
    REFUNDED,
    SHIPMENT_FAILED
}

fun Order.toOrderMessageRecord(): Record<String, OrderMessage> = Record.of(
    id.toString(),
    OrderMessage(
        payload = this,
        messageId = UUID.randomUUID()
    )
)