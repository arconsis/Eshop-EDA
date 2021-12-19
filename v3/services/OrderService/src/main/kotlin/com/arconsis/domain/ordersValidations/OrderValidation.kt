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
    VALIDATED,
    INVALID,
}

val OrderValidation.isValidated
    get() = type == OrderValidationType.VALIDATED

val OrderValidation.isInvalid
    get() = type == OrderValidationType.INVALID