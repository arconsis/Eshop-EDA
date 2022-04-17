package com.arconsis.domain.orders

import com.arconsis.domain.payments.Payment
import com.arconsis.domain.payments.PaymentStatus
import java.util.*

data class Order(
    val userId: UUID,
    val id: UUID,
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

fun Order.toPaymentFailed(transactionId: UUID?) = Payment(
    id = null,
    transactionId = transactionId,
    amount = amount,
    currency = currency,
    orderId = id,
    userId = userId,
    status = PaymentStatus.FAILED
)