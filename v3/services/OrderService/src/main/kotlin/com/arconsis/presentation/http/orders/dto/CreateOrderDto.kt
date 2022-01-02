package com.arconsis.presentation.http.orders.dto

import com.arconsis.domain.orders.CreateOrder
import java.util.*
import javax.validation.constraints.NotBlank

data class CreateOrderDto(
    @field:NotBlank
    val userId: UUID,
    @field:NotBlank
    val amount: Double,
    @field:NotBlank
    val currency: String,
    @field:NotBlank
    val productId: String,
    @field:NotBlank
    val quantity: Int,
)

fun CreateOrderDto.toCreateOrder() = CreateOrder(
    userId = userId,
    amount = amount,
    currency = currency,
    productId = productId,
    quantity = quantity
)