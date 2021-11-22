package com.arconsis.domain.shipments

import com.arconsis.data.outboxevents.OutboxEventEntityEvent
import com.arconsis.domain.outboxevents.AggregateType
import com.fasterxml.jackson.databind.ObjectMapper
import io.smallrye.reactive.messaging.kafka.Record
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

fun Shipment.toOutboxEventEntityEvent(): OutboxEventEntityEvent {
    val mapper = ObjectMapper()
    val payload = mapper.createObjectNode()
        .put("id", id.toString())
        .put("orderId", orderId.toString())
        .put("userId", userId.toString())
        .put("status", status.name)

    return OutboxEventEntityEvent(
        aggregateId = id,
        aggregateType = AggregateType.SHIPMENT,
        type = status.name,
        node = payload
    )
}