package com.arconsis.domain.ordersvalidations

import com.arconsis.data.OrdersRepository
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrderStatus
import com.arconsis.domain.orders.toOrderRecord
import com.arconsis.domain.orders.toOrderRecordWithStatus
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.groups.UniOnFailure
import io.smallrye.reactive.messaging.MutinyEmitter
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Channel
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrderValidationsService(
    @Channel("orders-out") private val emitter: MutinyEmitter<Record<String, Order>>,
    private val ordersRepository: OrdersRepository
) {

    fun handleOrderValidationEvents(orderValidation: OrderValidation): Uni<Void> {
        return when (orderValidation.status) {
            OrderValidationStatus.VALID -> {
                ordersRepository.updateOrder(orderValidation.orderId, OrderStatus.VALID)
                    .onFailure()
                    .handleValidOrderErrors(orderValidation.orderId)
                    .flatMap { order ->
                        val orderRecord = order.toOrderRecord()
                        sendOrderEvent(orderRecord)
                    }
            }
            OrderValidationStatus.INVALID -> {
                // TODO: Do we need to inform the user here about the out of stock ?
                ordersRepository.updateOrder(orderValidation.orderId, OrderStatus.OUT_OF_STOCK).replaceWithVoid()
            }
        }
    }

    private fun UniOnFailure<Order>.handleValidOrderErrors(orderId: UUID) = invoke { _ ->
        ordersRepository.getOrder(orderId)
            .flatMap { order ->
                val orderRecord = order.toOrderRecordWithStatus(status = OrderStatus.VALID)
                sendOrderEvent(orderRecord)
            }
    }



    private fun sendOrderEvent(orderRecord: Record<String, Order>) = emitter.send(orderRecord)
}