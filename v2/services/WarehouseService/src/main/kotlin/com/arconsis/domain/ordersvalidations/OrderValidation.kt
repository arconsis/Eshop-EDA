package com.arconsis.domain.ordersvalidations

import com.arconsis.domain.outboxevents.AggregateType
import com.arconsis.domain.outboxevents.CreateOutboxEvent
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.vertx.core.json.JsonObject
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
    payload = toJsonObject()
)

private fun OrderValidation.toJsonObject() = JsonObject()
    .put("productId", productId)
    .put("quantity", quantity)
    .put("orderId", orderId.toString())
    .put("userId", userId.toString())
    .put("status", status)