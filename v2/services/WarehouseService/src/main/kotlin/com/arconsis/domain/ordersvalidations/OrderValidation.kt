package com.arconsis.domain.ordersvalidations

import com.arconsis.data.outboxevents.OutboxEventEntityEvent
import com.arconsis.domain.outboxevents.AggregateType
import com.arconsis.domain.outboxevents.CreateOutboxEvent
import com.arconsis.domain.shipments.Shipment
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

fun OrderValidation.toOutboxEventEntityEvent(): OutboxEventEntityEvent {
    val mapper = ObjectMapper()
    val payload = mapper.createObjectNode()
        .put("productId", productId)
        .put("quantity", quantity)
        .put("orderId", orderId.toString())
        .put("userId", userId.toString())
        .put("status", status.name)

    return OutboxEventEntityEvent(
        aggregateId = orderId,
        aggregateType = AggregateType.SHIPMENT,
        type = status.name,
        node = payload
    )
}