package com.arconsis.presentation.events.shipments

import com.arconsis.domain.outboxevents.AggregateType
import com.arconsis.domain.shipments.Shipment
import com.arconsis.domain.shipments.ShipmentsService
import com.arconsis.presentation.events.common.WarehouseEventsDto
import com.arconsis.presentation.events.common.toOutboxEvent
import com.fasterxml.jackson.databind.ObjectMapper
import io.smallrye.mutiny.Uni
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Incoming
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ShipmentEventsResource(
    private val shipmentsService: ShipmentsService,
    private val objectMapper: ObjectMapper
) {
    @Incoming("warehouse-in")
    fun consumeShipmentEvents(shipmentRecord: Record<String, WarehouseEventsDto>): Uni<Void> {
        val shipmentEventDto = shipmentRecord.value()
        val outboxEvent = shipmentEventDto.payload.after.toOutboxEvent()
        if (outboxEvent.aggregateType != AggregateType.SHIPMENT) {
            return Uni.createFrom().voidItem()
        }
        val shipment = objectMapper.readValue(
            outboxEvent.payload,
            Shipment::class.java
        )
        return shipmentsService.handleShipmentEvents(shipment)
            .onFailure()
            .recoverWithNull()
    }
}