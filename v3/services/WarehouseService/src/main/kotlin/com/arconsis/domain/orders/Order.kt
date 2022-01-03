package com.arconsis.domain.orders

import java.util.*

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

val Order.isRequested
    get() = status == OrderStatus.REQUESTED

val Order.isPaid
    get() = status == OrderStatus.PAID

val Order.isPaymentFailed
    get() = status == OrderStatus.PAYMENT_FAILED
