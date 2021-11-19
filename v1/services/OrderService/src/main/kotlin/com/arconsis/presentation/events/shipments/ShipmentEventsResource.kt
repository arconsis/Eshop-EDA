package com.arconsis.presentation.events.shipments

import com.arconsis.domain.shipments.Shipment
import com.arconsis.domain.shipments.ShipmentsService
import io.smallrye.mutiny.Uni
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Incoming
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ShipmentEventsResource(private val shipmentsService: ShipmentsService) {

    @Incoming("shipments-in")
    fun consumeShipmentEvents(shipmentRecord: Record<String, Shipment>): Uni<Void> {
        val shipment = shipmentRecord.value()
        return shipmentsService.handleShipmentEvents(shipment)
            .onFailure()
            .recoverWithNull()
    }
}