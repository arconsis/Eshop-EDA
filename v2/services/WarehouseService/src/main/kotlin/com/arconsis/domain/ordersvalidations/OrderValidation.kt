package com.arconsis.domain.ordersvalidations

import java.util.*

data class OrderValidation(
    val productId: String,
    val quantity: Int,
    val orderId: UUID,
    val userId: UUID,
    val status: OrderValidationStatus,
)

enum class OrderValidationStatus {
    VALID,
    INVALID
}