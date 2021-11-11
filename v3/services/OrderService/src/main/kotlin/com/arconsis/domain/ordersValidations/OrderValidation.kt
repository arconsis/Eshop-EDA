package com.arconsis.domain.ordersValidations

import java.util.*

data class OrderValidation(
    val type: OrderValidationType,
    val productId: String,
    val quantity: Int,
    val orderId: UUID,
    val userId: UUID,
)

enum class OrderValidationType {
    VALID,
    INVALID,
}

val OrderValidation.isValid
    get() = type == OrderValidationType.VALID