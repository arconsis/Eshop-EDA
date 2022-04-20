package com.arconsis.domain.shipments

import java.util.*

data class Shipment(
    val id: UUID,
    val orderId: UUID,
    val userId: UUID,
    val status: ShipmentStatus,
)

enum class ShipmentStatus {
    PREPARING_SHIPMENT,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    FAILED,
}

val Shipment.isOutForShipment
    get() = status == ShipmentStatus.SHIPPED
