package com.arconsis.domain.shipments

import com.arconsis.data.outboxevents.OutboxEventsRepository
import com.arconsis.data.shipments.ShipmentsRepository
import com.fasterxml.jackson.databind.ObjectMapper
import io.smallrye.mutiny.coroutines.awaitSuspending
import javax.transaction.Transactional

class ShipmentsService(
    private val shipmentsRepository: ShipmentsRepository,
    private val outboxEventsRepository: OutboxEventsRepository,
    private val objectMapper: ObjectMapper,
) {

    @Transactional
    suspend fun updateShipment(updateShipment: UpdateShipment): Shipment {
        val shipment = shipmentsRepository.updateShipment(updateShipment).awaitSuspending()
        val createOutboxEvent = shipment.toCreateOutboxEvent(objectMapper)
        outboxEventsRepository.createEvent(createOutboxEvent).awaitSuspending()
        return shipment
    }
}