package com.arconsis.domain.shipments

import com.arconsis.domain.outboxevents.AggregateType
import com.arconsis.domain.outboxevents.CreateOutboxEvent
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.smallrye.reactive.messaging.kafka.Record
import io.vertx.core.json.JsonObject
import java.util.*

enum class ShipmentStatus {
    PREPARING_SHIPMENT,
    OUT_FOR_SHIPMENT,
    SHIPPED,
    CANCELLED,
}

data class Shipment(
    val id: UUID,
    val orderId: UUID,
    val status: ShipmentStatus,
    val userId: UUID
)

class UpdateShipment(val id: UUID, val status: ShipmentStatus)

class CreateShipment(val orderId: UUID, val userId: UUID, val status: ShipmentStatus)

fun Shipment.toShipmentRecord(): Record<String, Shipment> = Record.of(
    orderId.toString(),
    this
)

fun Shipment.toCreateOutboxEvent(objectMapper: ObjectMapper): CreateOutboxEvent = CreateOutboxEvent(
    aggregateType = AggregateType.SHIPMENT,
    aggregateId = this.id,
    payload = toJsonObject()
)

private fun Shipment.toJsonObject() = JsonObject()
    .put("id", id.toString())
    .put("orderId", orderId.toString())
    .put("userId", userId.toString())
    .put("status", status)