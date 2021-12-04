package com.arconsis.domain.shipments

import com.arconsis.data.common.toUni
import com.arconsis.data.outboxevents.OutboxEventsRepository
import com.arconsis.data.shipments.ShipmentsRepository
import com.fasterxml.jackson.databind.ObjectMapper
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ShipmentsService(
    private val shipmentsRepository: ShipmentsRepository,
    private val outboxEventsRepository: OutboxEventsRepository,
    private val objectMapper: ObjectMapper,
    private val sessionFactory: Mutiny.SessionFactory
) {
    fun updateShipment(updateShipment: UpdateShipment): Uni<Shipment> {
        return sessionFactory.withTransaction { session, _ ->
            shipmentsRepository.updateShipment(updateShipment, session)
                .flatMap { shipment ->
                    val createOutboxEvent = shipment.toCreateOutboxEvent(objectMapper)
                    outboxEventsRepository.createEvent(createOutboxEvent, session)
                    shipment.toUni()
                }
                .map {
                    it
                }
        }
    }
}