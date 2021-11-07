package com.arconsis.domain.shipments

import com.arconsis.data.shipments.ShipmentsRepository
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Channel
import org.eclipse.microprofile.reactive.messaging.Emitter
import javax.transaction.Transactional

class ShipmentsService(
    @Channel("shipment-out") private val emitter: Emitter<Record<String, Shipment>>,
    private val shipmentsRepository: ShipmentsRepository,
) {

    @Transactional
    fun updateShipment(updateShipment: UpdateShipment): Shipment {
        val shipment = this.shipmentsRepository.updateShipment(updateShipment)
        emitter.send(shipment.toShipmentRecord()).toCompletableFuture().get()
        return shipment
    }
}
