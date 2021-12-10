package com.arconsis.domain.orders

import com.arconsis.domain.ordervalidations.OrderValidation
import com.arconsis.domain.ordervalidations.OrderValidationEvent
import com.arconsis.domain.ordervalidations.OrderValidationType
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

fun Order.toOrderValidationEvent(type: OrderValidationType) = OrderValidationEvent(
    key = userId.toString(),
    value = OrderValidation(
        type = type,
        productId = productId,
        quantity = quantity,
        orderId = orderId,
        userId = userId
    )
)