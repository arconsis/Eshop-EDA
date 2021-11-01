package com.arconsis.domain.orders

import java.util.*

data class Order(
    val userId: UUID,
    val orderNo: UUID,
    val amount: String,
    val currency: String,
    val productId: String,
    val quantity: Int,
    val status: OrderStatus,
)

// TODO: Add the rest of the OrderStatus enum values
enum class OrderStatus {
    PENDING,
}

val Order.isPending
    get() = status == OrderStatus.PENDING
