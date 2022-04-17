package com.arconsis.domain.shipments

import com.arconsis.common.retryWithBackoff
import com.arconsis.data.processedevents.ProcessedEventsRepository
import com.arconsis.data.shipments.ShipmentsRepository
import com.arconsis.domain.orders.Order
import com.arconsis.domain.processedevents.ProcessedEvent
import io.smallrye.mutiny.coroutines.awaitSuspending
import io.smallrye.reactive.messaging.MutinyEmitter
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Channel
import org.hibernate.reactive.mutiny.Mutiny
import java.time.Instant
import java.util.*
import org.jboss.logging.Logger
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ShipmentsService(
    @Channel("shipment-out") private val emitter: MutinyEmitter<Record<String, ShipmentMessage>>,
    private val shipmentsRepository: ShipmentsRepository,
    private val processedEventsRepository: ProcessedEventsRepository,
    private val sessionFactory: Mutiny.SessionFactory,
    private val logger: Logger
) {

    suspend fun updateShipment(updateShipment: UpdateShipment): Shipment {
        val shipment = retryWithBackoff {
            shipmentsRepository.updateShipment(updateShipment).awaitSuspending()
        }
        sendShipmentEvent(shipment.toShipmentMessageRecord())
        return shipment
    }

    suspend fun proceedPaidOrder(messageId: UUID, order: Order) {
        logger.info("proceed paid order")
        val updatedShipment = sessionFactory.withTransaction { session, _ ->
            processedEventsRepository.createEvent(ProcessedEvent(messageId, Instant.now()), session)
                .flatMap {
                    shipmentsRepository.createShipment(
                        CreateShipment(
                            order.id,
                            order.userId,
                            ShipmentStatus.PREPARING_SHIPMENT
                        ), session
                    )
                }
                .flatMap {
                    shipmentsRepository.updateShipment(UpdateShipment(it.id, ShipmentStatus.SHIPPED), session)
                }
        }.onFailure()
            .recoverWithItem { _ ->
                logger.error("proceedPaidOrder for orderStatus ${order.status} failed and rolled back")
                null
            }.awaitSuspending()
        updatedShipment?.let {
            sendShipmentEvent(it.toShipmentMessageRecord())
        }
    }

    private suspend fun sendShipmentEvent(shipmentRecord: Record<String, ShipmentMessage>) {
        logger.info("Send shipment record $shipmentRecord")
        emitter.send(shipmentRecord).awaitSuspending()
    }
}