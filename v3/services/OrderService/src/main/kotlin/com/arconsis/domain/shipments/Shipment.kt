package com.arconsis.domain.shipments

import java.util.*

data class Shipment(
    val shipmentId: UUID,
    val orderId: UUID,
    val userId: UUID,
    val status: ShipmentStatus
)

enum class ShipmentStatus {
    PREPERING,
    OUT_FOR_SHIPMENT,
    SHIPPED
}

val Shipment.isOutForShipment
    get() = status == ShipmentStatus.OUT_FOR_SHIPMENT