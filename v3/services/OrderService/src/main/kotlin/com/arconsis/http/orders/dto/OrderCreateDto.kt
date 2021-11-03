package com.arconsis.http.orders.dto

import java.util.*

data class OrderCreateDto(
	val userId: UUID,
	val amount: String,
	val currency: String,
	val productId: String,
	val quantity: Int,
)