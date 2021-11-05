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
    val userId: UUID,
    val orderId: UUID,
    val amount: Double,
    val currency: String,
    val productId: String,
    val quantity: Int,
    val status: OrderStatus,
)

enum class OrderStatus {
    PENDING,
    VALID,
    OUT_OF_STOCK,
    PAID,
    OUT_FOR_SHIPMENT,
    COMPLETED,
    PAYMENT_FAILED,
    CANCELLED,
    REFUNDED
}

fun Order.toOrderRecord(): Record<String, Order> = Record.of(
    userId.toString(),
    this
)
