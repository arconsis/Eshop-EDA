package com.arconsis.domain.shipments

import com.arconsis.domain.orders.Order
import com.arconsis.domain.outboxevents.AggregateType
import com.arconsis.domain.outboxevents.CreateOutboxEvent
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
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
    SHIPPED,
    DELIVERED
}

val Shipment.isOutForShipment
    get() = status == ShipmentStatus.SHIPPED

class ShipmentDeserializer : ObjectMapperDeserializer<Shipment>(Shipment::class.java)