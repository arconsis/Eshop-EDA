package com.arconsis.domain.ordervalidations

import java.util.*

data class OrderValidation(
    val type: OrderValidationType,
    val productId: String,
    val quantity: Int,
    val orderId: UUID,
    val userId: UUID,
)

enum class OrderValidationType {
    VALIDATED,
    INVALID,
}

fun OrderValidation.toOrderValidationEvent() = OrderValidationEvent(
    key = userId.toString(),
    value = OrderValidation(
        type = type,
        productId = productId,
        quantity = quantity,
        orderId = orderId,
        userId = userId
    )
)