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
    PENDING,
    VALID,
    OUT_OF_STOCK,
    PAID,
    OUT_FOR_SHIPMENT,
    COMPLETED,
    PAYMENT_FAILED,
    CANCELLED,
    REFUNDED,
    SHIPMENT_FAILED
}

fun Order.toOrderRecord(): Record<String, Order> = Record.of(
    id.toString(),
    this
)

fun Order.toOrderRecordWithStatus(status: OrderStatus): Record<String, Order> = Record.of(
    id.toString(),
    this.copy(status = status)
)