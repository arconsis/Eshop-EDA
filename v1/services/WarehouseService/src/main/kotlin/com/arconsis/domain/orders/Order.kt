package com.arconsis.domain.orders

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer
import java.util.*

data class Order(
    val id: UUID,
    val userId: UUID,
    val amount: Double,
    val currency: String,
    val productId: String,
    val quantity: Int,
    val status: OrderStatus,
)

enum class OrderStatus {
    PENDING,
    VALID,
    OUT_OF_STOCK,
    PAID,
    OUT_FOR_SHIPMENT,
    COMPLETED,
    PAYMENT_FAILED,
    CANCELLED,
    REFUNDED
}

class OrdersDeserializer : ObjectMapperDeserializer<Order>(Order::class.java)
