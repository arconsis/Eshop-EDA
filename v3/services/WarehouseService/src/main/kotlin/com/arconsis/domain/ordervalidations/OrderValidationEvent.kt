package com.arconsis.domain.ordervalidations

data class OrderValidationEvent(
    val key: String,
    val value: OrderValidation,
)
