package com.arconsis.presentation.http.orders.dto

import java.util.*

data class OrderCreateDto(
    val userId: UUID,
    val amount: Double,
    val currency: String,
    val productId: String,
    val quantity: Int,
)