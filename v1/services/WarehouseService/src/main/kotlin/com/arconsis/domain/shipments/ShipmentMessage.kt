package com.arconsis.domain.shipments

import com.arconsis.domain.message.Message
import java.util.*

data class ShipmentMessage(override val payload: Shipment, override val messageId: UUID) : Message<Shipment>