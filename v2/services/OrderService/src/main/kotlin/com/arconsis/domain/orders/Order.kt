package com.arconsis.domain.orders

import com.arconsis.domain.outboxevents.AggregateType
import com.arconsis.domain.outboxevents.CreateOutboxEvent
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.smallrye.reactive.messaging.kafka.Record
import io.vertx.core.json.JsonObject
import java.util.*

data class CreateOrder(
    val userId: UUID,
    val amount: Double,
    val currency: String,
    val productId: String,
    val quantity: Int,
)

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

private fun Order.toJsonObject() = JsonObject()
    .put("id", id.toString())
    .put("userId", userId.toString())
    .put("amount", amount)
    .put("currency", currency)
    .put("productId", productId)
    .put("quantity", quantity)
    .put("status", status)

fun Order.toCreateOutboxEvent(): CreateOutboxEvent = CreateOutboxEvent(
    aggregateType = AggregateType.ORDER,
    aggregateId = this.id,
    payload = toJsonObject()
)