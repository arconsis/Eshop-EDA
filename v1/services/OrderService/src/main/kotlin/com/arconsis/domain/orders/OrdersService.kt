package com.arconsis.domain.orders

import com.arconsis.data.orders.OrdersRepository
import com.arconsis.data.processedevents.ProcessedEventsRepository
import com.arconsis.domain.processedevents.ProcessedEvent
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
    @Channel("orders-out") private val emitter: MutinyEmitter<Record<String, OrderMessage>>,
    private val ordersRepository: OrdersRepository,
    private val processedEventsRepository: ProcessedEventsRepository,
    private val sessionFactory: Mutiny.SessionFactory,
    private val logger: Logger,
) {
    suspend fun getOrder(orderId: UUID): Order = ordersRepository.getOrder(orderId).awaitSuspending()

    suspend fun createOrder(createOrder: CreateOrder): Order {
        val order = ordersRepository.createOrder(createOrder).awaitSuspending()
        val orderMessageRecord = order.toOrderMessageRecord()
        emitter.send(orderMessageRecord).awaitSuspending()
        return order
    }

    suspend fun updateAndSendOrder(messageId: UUID, orderId: UUID, orderStatus: OrderStatus) {
        /*
            Tradeoff we got in order to focus on event-driven architecture patterns.
            Ofc sessionFactory.withTransaction is not part of domain services, as it
            binds it with databases. On the other hand does not belong to data / repositories
            as is business decision to have Proceed_Events table for deduplication.
            TODO: when hibernate reactive starts supporting @Transactional we should start using it
            TODO: re-think retry process and DLQ
        */
        val order = sessionFactory.withTransaction { session, _ ->
            processedEventsRepository.createEvent(ProcessedEvent(messageId, Instant.now()), session)
                .flatMap {
                    ordersRepository.updateOrder(orderId, orderStatus, session)
                }
        }.onFailure()
            .recoverWithItem { _ ->
                logger.error("updateAndSendOrder for orderStatus $orderStatus failed and rolled back")
                null
            }.awaitSuspending()
        order?.let {
            val orderMessageRecord = it.toOrderMessageRecord()
            sendOrderEvent(orderMessageRecord)
        }
    }

    private suspend fun sendOrderEvent(orderRecord: Record<String, OrderMessage>) {
        logger.info("Send order record ${orderRecord.value()}")
        emitter.send(orderRecord).awaitSuspending()
    }
}