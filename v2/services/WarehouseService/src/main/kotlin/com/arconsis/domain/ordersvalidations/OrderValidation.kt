package com.arconsis.domain.ordersvalidations

import com.arconsis.domain.outboxevents.AggregateType
import com.arconsis.domain.outboxevents.CreateOutboxEvent
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.*

data class OrderValidation(
    val productId: String,
    val quantity: Int,
    val orderId: UUID,
    val userId: UUID,
    val status: OrderValidationStatus,
)

enum class OrderValidationStatus {
    VALID,
    INVALID
}

fun OrderValidation.toCreateOutboxEvent(objectMapper: ObjectMapper): CreateOutboxEvent = CreateOutboxEvent(
    aggregateType = AggregateType.ORDER_VALIDATION,
    aggregateId = this.orderId,
    type = this.status.toString(),
    payload = objectMapper.writeValueAsString(this)
)