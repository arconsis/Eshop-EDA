package com.arconsis.domain.inventory

import com.arconsis.data.inventory.InventoryRepository
import com.arconsis.data.processedevents.ProcessedEventsRepository
import com.arconsis.domain.orders.Order
import com.arconsis.domain.ordersvalidations.OrderValidation
import com.arconsis.domain.ordersvalidations.OrderValidationMessage
import com.arconsis.domain.ordersvalidations.OrderValidationStatus
import com.arconsis.domain.ordersvalidations.toOrderValidationMessageRecord
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
class InventoryService(
    @Channel("order-validation-out") private val orderValidationEmitter: MutinyEmitter<Record<String, OrderValidationMessage>>,
    private val inventoryRepository: InventoryRepository,
    private val processedEventsRepository: ProcessedEventsRepository,
    private val sessionFactory: Mutiny.SessionFactory,
    private val logger: Logger
) {

    suspend fun getInventory(id: UUID): Inventory? {
        return inventoryRepository.getInventory(id)
    }

    suspend fun createInventory(createInventory: CreateInventory): Inventory {
        return inventoryRepository.createInventory(createInventory)
    }

    suspend fun updateInventory(updateInventory: UpdateInventory): Inventory {
        return inventoryRepository.updateInventory(updateInventory)
    }

    /*
       Tradeoff we got in order to focus on event-driven architecture patterns.
       Ofc sessionFactory.withTransaction is not part of domain services, as it
       binds it with databases. On the other hand does not belong to data / repositories
       as is business decision to have Proceed_Events table for deduplication.
       TODO: when hibernate reactive starts supporting @Transactional we should start using it
       TODO: re-think database retry process and DLQ
    */
    suspend fun proceedRequestedOrder(messageId: UUID, order: Order) {
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

    suspend fun proceedFailedPaymentOrder(eventId: UUID, order: Order) {
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

    private suspend fun handleInventoryStockError(messageId: UUID, order: Order) {
        val stockUpdated = processedEventsRepository.createEvent(ProcessedEvent(messageId, Instant.now()))
            .map {
                false
            }.awaitSuspending()
        sendOrderValidationEvent(stockUpdated, order)
    }

    private suspend fun sendOrderValidationEvent(stockUpdated: Boolean, order: Order) {
        val orderValidationMessage = OrderValidation(
            productId = order.productId,
            quantity = order.quantity,
            orderId = order.id,
            userId = order.userId,
            status = if (stockUpdated) OrderValidationStatus.VALIDATED else OrderValidationStatus.INVALID
        ).toOrderValidationMessageRecord()
        logger.info("Send order validation record: $orderValidationMessage")
        orderValidationEmitter.send(orderValidationMessage).awaitSuspending()
    }

    companion object {
        const val INVENTORY_STOCK_ERROR =
            "new row for relation \"inventory\" violates check constraint \"inventory_stock_check\""
    }
}