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