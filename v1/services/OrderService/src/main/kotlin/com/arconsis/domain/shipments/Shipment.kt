package com.arconsis.domain.shipments

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer
import java.util.*

data class Shipment(
	val id: UUID,
	val orderId: UUID,
	val userId: UUID,
	val status: ShipmentStatus,
)

enum class ShipmentStatus {
    PREPARING,
    OUT_FOR_SHIPMENT,
    SHIPPED
}

val Shipment.isOutForShipment
    get() = status == ShipmentStatus.OUT_FOR_SHIPMENT

class ShipmentDeserializer : ObjectMapperDeserializer<Shipment>(Shipment::class.java)
