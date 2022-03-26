package com.arconsis.presentation.events.shipments

import com.arconsis.domain.shipments.Shipment
import com.arconsis.domain.shipments.ShipmentsService
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Incoming
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ShipmentEventsResource(private val shipmentsService: ShipmentsService) {

    @Incoming("shipments-in")
    suspend fun consumeShipmentEvents(shipmentRecord: Record<String, Shipment>) {
        // TODO: Log the possible error here
        val shipment = shipmentRecord.value()
        runCatching {
            shipmentsService.handleShipmentEvents(shipment)
        }.getOrNull()
    }
}