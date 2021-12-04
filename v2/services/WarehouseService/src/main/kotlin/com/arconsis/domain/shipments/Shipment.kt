package com.arconsis.domain.shipments

import com.arconsis.domain.outboxevents.AggregateType
import com.arconsis.domain.outboxevents.CreateOutboxEvent
import com.arconsis.domain.outboxevents.OutboxEventType
import com.fasterxml.jackson.databind.ObjectMapper
import io.smallrye.reactive.messaging.kafka.Record
import java.util.*

enum class ShipmentStatus {
    PREPARING_SHIPMENT,
    SHIPPED,
    DELIVERED,
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
    type = this.status.toOutboxEventType(),
    payload = objectMapper.writeValueAsString(this)
)

private fun ShipmentStatus.toOutboxEventType(): OutboxEventType = when (this) {
    ShipmentStatus.PREPARING_SHIPMENT -> OutboxEventType.SHIPMENT_PREPARING_SHIPMENT
    ShipmentStatus.SHIPPED -> OutboxEventType.SHIPMENT_SHIPPED
    ShipmentStatus.DELIVERED -> OutboxEventType.SHIPMENT_DELIVERED
    ShipmentStatus.CANCELLED -> OutboxEventType.SHIPMENT_CANCELLED
}