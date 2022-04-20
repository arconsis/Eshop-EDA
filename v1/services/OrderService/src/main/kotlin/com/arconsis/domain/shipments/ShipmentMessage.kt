package com.arconsis.domain.shipments

import com.arconsis.domain.message.Message
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer
import java.util.*

data class ShipmentMessage(override val payload: Shipment, override val messageId: UUID) : Message<Shipment>

class ShipmentMessageDeserializer : ObjectMapperDeserializer<ShipmentMessage>(ShipmentMessage::class.java)
