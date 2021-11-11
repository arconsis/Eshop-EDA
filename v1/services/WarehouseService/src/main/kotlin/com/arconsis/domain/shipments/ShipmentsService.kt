package com.arconsis.domain.shipments

import com.arconsis.data.shipments.ShipmentsRepository
import io.smallrye.mutiny.coroutines.awaitSuspending
import io.smallrye.reactive.messaging.MutinyEmitter
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Channel

class ShipmentsService(
    @Channel("shipment-out") private val emitter: MutinyEmitter<Record<String, Shipment>>,
    private val shipmentsRepository: ShipmentsRepository,
) {

    suspend fun updateShipment(updateShipment: UpdateShipment): Shipment {
        val shipment = this.shipmentsRepository.updateShipment(updateShipment)
        emitter.send(shipment.toShipmentRecord()).awaitSuspending()
        return shipment
    }
}