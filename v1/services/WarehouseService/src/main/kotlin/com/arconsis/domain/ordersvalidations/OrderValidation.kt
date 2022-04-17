package com.arconsis.domain.ordersvalidations

import io.smallrye.reactive.messaging.kafka.Record
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

fun OrderValidation.toOrderValidationMessageRecord(): Record<String, OrderValidationMessage> = Record.of(
    orderId.toString(),
    OrderValidationMessage(
        payload = this,
        messageId = UUID.randomUUID()
    )
)