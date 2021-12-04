package com.arconsis.domain.ordersvalidations

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer
import java.util.*

data class OrderValidation(
    val productId: String,
    val quantity: Int,
    val orderId: UUID,
    val userId: UUID,
    val status: OrderValidationStatus,
)

enum class OrderValidationStatus {
    VALIDATED,
    INVALID
}

class OrderValidationDeserializer :
    ObjectMapperDeserializer<OrderValidation>(OrderValidation::class.java)