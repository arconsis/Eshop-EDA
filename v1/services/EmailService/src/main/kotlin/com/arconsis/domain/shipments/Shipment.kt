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
    PREPERING,
    OUT_FOR_SHIPMENT,
    SHIPPED
}

class ShipmentsDeserializer : ObjectMapperDeserializer<Shipment>(Shipment::class.java)