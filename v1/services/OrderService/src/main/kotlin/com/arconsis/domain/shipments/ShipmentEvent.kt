package com.arconsis.domain.shipments

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer

enum class ShipmentType {
    SHIPMENT_SHIPPED,
    SHIPMENT_PREPARED
}

class ShipmentMessage(override val type: ShipmentType, override val payload: Shipment) : Message<ShipmentType, Shipment>

class ShipmentMessageDeserializer : ObjectMapperDeserializer<ShipmentMessage>(ShipmentMessage::class.java)