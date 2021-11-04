package com.arconsis.domain.shipments

import java.util.*

data class Shipment(
    val shipmentId: UUID,
    val orderId: UUID,
    val userId: UUID,
    val userEmail: String,
    val status: ShipmentStatus,
)

enum class ShipmentStatus {
    PREPERING,
    OUT_FOR_SHIPMENT,
    SHIPPED
}
