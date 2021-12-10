package com.arconsis.domain.orders

import com.arconsis.presentation.http.orders.dto.OrderCreateDto
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

fun OrderCreateDto.toPendingOrder() = Order(
    userId = userId,
    orderId = UUID.randomUUID(),
    amount = amount,
    currency = currency,
    productId = productId,
    quantity = quantity,
    status = OrderStatus.REQUESTED
)