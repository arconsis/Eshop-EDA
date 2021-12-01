package com.arconsis.domain.shipments

import io.smallrye.reactive.messaging.kafka.Record
import java.util.*

enum class ShipmentStatus {
    PREPARING_SHIPMENT,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    FAILED,
}

data class Shipment(
    val id: UUID?,
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