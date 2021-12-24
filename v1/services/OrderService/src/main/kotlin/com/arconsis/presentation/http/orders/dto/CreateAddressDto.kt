package com.arconsis.presentation.http.orders.dto

import com.arconsis.domain.orders.CreateOrder
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*
import javax.validation.constraints.NotBlank

data class CreateOrderDto(
    @field:NotBlank
    @JsonProperty("userId") val userId: UUID,
    @field:NotBlank
    @JsonProperty("amount") val amount: Double,
    @field:NotBlank
    @JsonProperty("currency") val currency: String,
    @field:NotBlank
    @JsonProperty("productId") val productId: String,
    @field:NotBlank
    @JsonProperty("quantity") val quantity: Int,
)

fun CreateOrderDto.toCreateOrder() = CreateOrder(
    userId = userId,
    amount = amount,
    currency = currency,
    productId = productId,
    quantity = quantity
)