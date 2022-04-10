package com.arconsis.domain.shipments

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
class ShipmentsService(
    @Channel("orders-out") private val emitter: MutinyEmitter<Record<String, Order>>,
    private val ordersRepository: OrdersRepository,
    private val logger: Logger
) {
    suspend fun handleShipmentEvents(shipment: Shipment) {
        when (shipment.status) {
            ShipmentStatus.DELIVERED -> updateAndSendOrder(shipment.orderId, OrderStatus.COMPLETED)
            ShipmentStatus.SHIPPED -> updateAndSendOrder(shipment.orderId, OrderStatus.SHIPPED)
            ShipmentStatus.FAILED -> updateAndSendOrder(shipment.orderId, OrderStatus.SHIPMENT_FAILED)
            else -> return
        }
    }

    private suspend fun updateAndSendOrder(orderId: UUID, orderStatus: OrderStatus) {
        val order = runCatching {
            retryWithBackoff {
                ordersRepository.updateOrder(orderId, orderStatus)
            }
        }.getOrElse {
            logger.error(it)
            ordersRepository.getOrder(orderId).copy(status = orderStatus)
        }

        val orderRecord = order.toOrderRecord()
        sendOrderEvent(orderRecord)
    }

    private suspend fun sendOrderEvent(orderRecord: Record<String, Order>) {
        logger.info("Send order record ${orderRecord.value()}")
        emitter.send(orderRecord).awaitSuspending()
    }
}