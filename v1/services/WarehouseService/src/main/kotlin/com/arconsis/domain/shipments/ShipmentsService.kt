package com.arconsis.domain.shipments

import com.arconsis.common.retryWithBackoff
import com.arconsis.data.shipments.ShipmentsRepository
import io.smallrye.mutiny.coroutines.awaitSuspending
import io.smallrye.reactive.messaging.MutinyEmitter
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Channel

class ShipmentsService(
    @Channel("shipment-out") private val emitter: MutinyEmitter<Record<String, ShipmentMessage>>,
    private val shipmentsRepository: ShipmentsRepository
) {

    suspend fun updateShipment(updateShipment: UpdateShipment): Shipment {
        val shipment = retryWithBackoff {
            shipmentsRepository.updateShipment(updateShipment).awaitSuspending()
        }
        sendShipmentEvent(shipment.toShipmentMessageRecord())
        return shipment
    }

    private suspend fun sendShipmentEvent(shipmentRecord: Record<String, ShipmentMessage>) {
        print { "Send shipment record $shipmentRecord" }
        emitter.send(shipmentRecord).awaitSuspending()
    }
}