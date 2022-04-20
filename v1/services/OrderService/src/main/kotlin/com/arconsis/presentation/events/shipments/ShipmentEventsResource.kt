package com.arconsis.presentation.events.shipments

import com.arconsis.domain.shipments.ShipmentMessage
import com.arconsis.domain.shipments.ShipmentsService
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Incoming
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ShipmentEventsResource(private val shipmentsService: ShipmentsService) {
    @Incoming("shipments-in")
    suspend fun consumeShipmentEvents(shipmentMessageRecord: Record<String, ShipmentMessage>) {
        // TODO: Log the possible error here
        val shipmentMessage = shipmentMessageRecord.value()
        runCatching {
            shipmentsService.handleShipmentEvents(shipmentMessage)
        }.getOrNull()
    }
}