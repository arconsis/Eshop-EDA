package com.arconsis.domain.shipments

import com.arconsis.data.orders.OrdersRepository
import com.arconsis.data.outboxevents.OutboxEventsRepository
import com.arconsis.domain.orders.OrderStatus
import com.arconsis.domain.orders.toCreateOutboxEvent
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ShipmentsService(
    private val ordersRepository: OrdersRepository,
    private val outboxEventsRepository: OutboxEventsRepository,
    private val sessionFactory: Mutiny.SessionFactory
) {
    fun handleShipmentEvents(shipment: Shipment): Uni<Void> {
        return when (shipment.status) {
            ShipmentStatus.SHIPPED -> handleShippedShipment(shipment)
            ShipmentStatus.OUT_FOR_SHIPMENT -> handleOutForShipment(shipment)
            else -> return Uni.createFrom().voidItem()
        }
    }

    private fun handleShippedShipment(shipment: Shipment): Uni<Void> {
        return sessionFactory.withTransaction { session, _ ->
            ordersRepository.updateOrder(shipment.orderId, OrderStatus.COMPLETED, session)
                .flatMap { order ->
                    val createOutboxEvent = order.toCreateOutboxEvent()
                    outboxEventsRepository.createEvent(createOutboxEvent, session)
                }
                .map {
                    null
                }
        }
    }

    private fun handleOutForShipment(shipment: Shipment): Uni<Void> {
        return sessionFactory.withTransaction { session, _ ->
            ordersRepository.updateOrder(shipment.orderId, OrderStatus.OUT_FOR_SHIPMENT, session)
                .flatMap { order ->
                    val createOutboxEvent = order.toCreateOutboxEvent()
                    outboxEventsRepository.createEvent(createOutboxEvent, session)
                }
                .map {
                    null
                }
        }
    }
}