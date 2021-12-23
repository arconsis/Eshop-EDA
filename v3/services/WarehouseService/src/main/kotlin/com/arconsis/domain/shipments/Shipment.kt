package com.arconsis.domain.shipments

import java.util.*

data class Shipment(
    val shipmentId: UUID,
    val orderId: UUID,
    val userId: UUID,
    val status: ShipmentStatus
)

enum class ShipmentStatus {
    SHIPPED,
    DELIVERED,
    FAILED
}

val Shipment.isOutForShipment
    get() = status == ShipmentStatus.SHIPPED

val Shipment.isDelivered
    get() = status == ShipmentStatus.DELIVERED

val Shipment.failed
    get() = status == ShipmentStatus.FAILED