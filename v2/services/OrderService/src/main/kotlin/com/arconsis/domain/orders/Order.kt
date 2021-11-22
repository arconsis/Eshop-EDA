package com.arconsis.domain.orders

import com.arconsis.data.outboxevents.OutboxEventEntityEvent
import com.arconsis.domain.outboxevents.AggregateType
import com.fasterxml.jackson.databind.ObjectMapper
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


fun Order.toOutboxEventEntityEvent(): OutboxEventEntityEvent {
    val mapper = ObjectMapper()
    val payload = mapper.createObjectNode()
        .put("id", id.toString())
        .put("userId", userId.toString())
        .put("amount", amount)
        .put("currency", currency)
        .put("productId", productId)
        .put("quantity", quantity)
        .put("status", status.name)
    return OutboxEventEntityEvent(
        aggregateId = id,
        aggregateType = AggregateType.ORDER,
        type = status,
        node = payload
    )
}