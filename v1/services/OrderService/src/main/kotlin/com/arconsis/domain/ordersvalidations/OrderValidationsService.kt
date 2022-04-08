package com.arconsis.domain.ordersvalidations

import com.arconsis.common.retryWithBackoff
import com.arconsis.data.OrdersRepository
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrderStatus
import com.arconsis.domain.orders.toOrderRecord
import io.smallrye.mutiny.coroutines.awaitSuspending
import io.smallrye.reactive.messaging.MutinyEmitter
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Channel
import org.jboss.logging.Logger
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrderValidationsService(
    @Channel("orders-out") private val emitter: MutinyEmitter<Record<String, Order>>,
    private val ordersRepository: OrdersRepository,
    private val logger: Logger
) {

    suspend fun handleOrderValidationEvents(orderValidation: OrderValidation) {
        when (orderValidation.status) {
            OrderValidationStatus.VALIDATED -> {
                val order = updateOrder(orderValidation.orderId, OrderStatus.VALIDATED)
                val orderRecord = order.toOrderRecord()
                sendOrderEvent(orderRecord)
            }
            OrderValidationStatus.INVALID -> updateOrder(orderValidation.orderId, OrderStatus.OUT_OF_STOCK)
        }
    }

    private suspend fun updateOrder(orderId: UUID, orderStatus: OrderStatus): Order {
        return runCatching {
            retryWithBackoff {
                ordersRepository.updateOrder(orderId, orderStatus)
            }
        }.getOrElse {
            ordersRepository.getOrder(orderId).copy(status = orderStatus)
        }
    }

    private suspend fun sendOrderEvent(orderRecord: Record<String, Order>) {
        logger.info("Send order record ${orderRecord.value()}")
        emitter.send(orderRecord).awaitSuspending()
    }
}