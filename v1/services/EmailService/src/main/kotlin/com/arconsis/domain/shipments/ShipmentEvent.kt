package com.arconsis.domain.shipments

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer

data class ShipmentEvent(
    val key: String,
    val value: Shipment,
)

class ShipmentsEventsDeserializer : ObjectMapperDeserializer<ShipmentEvent>(ShipmentEvent::class.java)
