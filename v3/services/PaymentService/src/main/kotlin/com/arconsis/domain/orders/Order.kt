package com.arconsis.domain.orders

import com.arconsis.domain.payments.CreatePayment
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

val Order.isValidated
    get() = status == OrderStatus.VALIDATED

fun Order.toCreatePayment() = CreatePayment(
    userId = userId,
    orderId = orderId,
    amount = amount,
    currency = currency,
)