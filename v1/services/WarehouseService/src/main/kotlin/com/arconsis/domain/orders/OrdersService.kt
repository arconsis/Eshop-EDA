package com.arconsis.domain.orders

import com.arconsis.data.inventory.InventoryRepository
import com.arconsis.data.processedevents.ProcessedEventsRepository
import com.arconsis.data.shipments.ShipmentsRepository
import com.arconsis.domain.ordersvalidations.OrderValidation
import com.arconsis.domain.ordersvalidations.OrderValidationMessage
import com.arconsis.domain.ordersvalidations.OrderValidationStatus
import com.arconsis.domain.ordersvalidations.toOrderValidationMessageRecord
import com.arconsis.domain.processedevents.ProcessedEvent
import com.arconsis.domain.shipments.*
import io.smallrye.mutiny.coroutines.awaitSuspending
import io.smallrye.reactive.messaging.MutinyEmitter
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Channel
import org.hibernate.reactive.mutiny.Mutiny
import org.jboss.logging.Logger
import java.time.Instant
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrdersService(
    @Channel("shipment-out") private val shipmentEmitter: MutinyEmitter<Record<String, ShipmentMessage>>,
    @Channel("order-validation-out") private val orderValidationEmitter: MutinyEmitter<Record<String, OrderValidationMessage>>,
    private val inventoryRepository: InventoryRepository,
    private val processedEventsRepository: ProcessedEventsRepository,
    private val shipmentsRepository: ShipmentsRepository,
    private val sessionFactory: Mutiny.SessionFactory,
    private val logger: Logger
) {
    suspend fun handleOrderEvents(orderMessage: OrderMessage) {
        val order = orderMessage.payload
        val messageId = orderMessage.messageId
        return when (order.status) {
            OrderStatus.REQUESTED -> proceedRequestedOrder(messageId, order)
            OrderStatus.PAID -> proceedPaidOrder(messageId, order)
            OrderStatus.PAYMENT_FAILED -> proceedFailedPaymentOrder(messageId, order)
            else -> return
        }
    }

    private suspend fun proceedRequestedOrder(messageId: UUID, order: Order) {
        logger.info("proceed requested order")
        runCatching {
            val stockUpdated = sessionFactory.withTransaction { session, _ ->
                processedEventsRepository.createEvent(ProcessedEvent(messageId, Instant.now()), session)
                    .flatMap {
                        inventoryRepository.reserveProductStock(order.productId, order.quantity, session)
                    }
            }.awaitSuspending()
            logger.info("stockUpdated $stockUpdated")
            stockUpdated?.let {
                sendOrderValidationEvent(it, order)
            }
        }.getOrElse { err ->
            logger.error("proceedValidatedOrder failed with error: ${err.localizedMessage}")
            if (err.message?.contains(INVENTORY_STOCK_ERROR) == true) {
                handleInventoryStockError(messageId, order)
            }
        }
    }

    private suspend fun proceedPaidOrder(messageId: UUID, order: Order) {
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

    private suspend fun proceedFailedPaymentOrder(eventId: UUID, order: Order) {
        logger.info("proceed failed paid order")
        sessionFactory.withTransaction { session, _ ->
            processedEventsRepository.createEvent(ProcessedEvent(eventId, Instant.now()), session)
                .flatMap {
                    inventoryRepository.increaseProductStock(order.productId, order.quantity, session)
                }
        }.onFailure()
            .recoverWithItem { _ ->
                logger.error("proceedFailedPaymentOrder for orderStatus ${order.status} failed and rolled back")
                null
            }.awaitSuspending()
    }

    private suspend fun sendShipmentEvent(shipmentRecord: Record<String, ShipmentMessage>) {
        logger.info("Send shipment record $shipmentRecord")
        shipmentEmitter.send(shipmentRecord).awaitSuspending()
    }

    private suspend fun handleInventoryStockError(messageId: UUID, order: Order) {
        val stockUpdated = processedEventsRepository.createEvent(ProcessedEvent(messageId, Instant.now()))
            .map {
                false
            }.awaitSuspending()
        sendOrderValidationEvent(stockUpdated, order)
    }

    private suspend fun sendOrderValidationEvent(stockUpdated: Boolean, order: Order) {
        val orderValidation = OrderValidation(
            productId = order.productId,
            quantity = order.quantity,
            orderId = order.id,
            userId = order.userId,
            status = if (stockUpdated) OrderValidationStatus.VALIDATED else OrderValidationStatus.INVALID
        )
        orderValidationEmitter.send(orderValidation.toOrderValidationMessageRecord()).awaitSuspending()
    }

    companion object {
        const val INVENTORY_STOCK_ERROR =
            "new row for relation \"inventory\" violates check constraint \"inventory_stock_check\""
    }
}