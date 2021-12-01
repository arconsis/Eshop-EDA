package com.arconsis.domain.ordersvalidations

import com.arconsis.common.retryWithBackoff
import com.arconsis.data.OrdersRepository
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrderStatus
import com.arconsis.domain.orders.toOrderRecord
import com.arconsis.domain.orders.toOrderRecordWithStatus
import io.smallrye.mutiny.Uni
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

    fun handleOrderValidationEvents(orderValidation: OrderValidation): Uni<Void> {
        return when (orderValidation.status) {
            OrderValidationStatus.VALIDATED -> {
                ordersRepository.updateOrder(orderValidation.orderId, OrderStatus.VALIDATED)
                    .handleUpdateOrderErrors(orderValidation.orderId, OrderStatus.VALIDATED)
                    .flatMap { order ->
                        val orderRecord = order.toOrderRecord()
                        sendOrderEvent(orderRecord)
                    }
            }
            OrderValidationStatus.INVALID -> {
                ordersRepository.updateOrder(orderValidation.orderId, OrderStatus.OUT_OF_STOCK)
                    .retryWithBackoff()
                    .replaceWithVoid()
            }
        }
    }

    private fun Uni<Order>.handleUpdateOrderErrors(orderId: UUID, orderStatus: OrderStatus) = retryWithBackoff()
        .onFailure()
        .call { _ ->
            ordersRepository.getOrder(orderId)
                .retryWithBackoff()
                .flatMap { order ->
                    val orderRecord = order.toOrderRecordWithStatus(status = orderStatus)
                    sendOrderEvent(orderRecord)
                }
        }

    private fun sendOrderEvent(orderRecord: Record<String, Order>): Uni<Void> {
        logger.info("Send order record ${orderRecord.value()}")
        return emitter.send(orderRecord)
    }
}