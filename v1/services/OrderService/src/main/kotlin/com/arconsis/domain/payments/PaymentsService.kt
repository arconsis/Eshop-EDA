package com.arconsis.domain.payments

import com.arconsis.common.retryWithBackoff
import com.arconsis.common.toUni
import com.arconsis.data.OrdersRepository
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrderStatus
import com.arconsis.domain.orders.toOrderRecord
import com.arconsis.domain.orders.toOrderRecordWithStatus
import io.smallrye.mutiny.Uni
import io.smallrye.reactive.messaging.MutinyEmitter
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Channel
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PaymentsService(
    @Channel("orders-out") private val emitter: MutinyEmitter<Record<String, Order>>,
    private val ordersRepository: OrdersRepository,
) {
    fun handlePaymentEvents(payment: Payment): Uni<Void> {
        return when (payment.status) {
            PaymentStatus.SUCCESS -> {
                ordersRepository.updateOrder(payment.orderId, OrderStatus.PAID)
                    .handleUpdateOrderErrors(payment.orderId, OrderStatus.PAID)
                    .flatMap { order ->
                        sendOrderEvent(order.toOrderRecord())
                    }
            }
            PaymentStatus.FAILED -> {
                ordersRepository.updateOrder(payment.orderId, OrderStatus.PAYMENT_FAILED)
                    .handleUpdateOrderErrors(payment.orderId, OrderStatus.PAYMENT_FAILED)
                    .flatMap { order ->
                        val orderRecord = order.toOrderRecord()
                        emitter.send(orderRecord)
                    }
            }
            else -> return Uni.createFrom().voidItem()
        }
    }

    private fun Uni<Order>.handleUpdateOrderErrors(orderId: UUID, orderStatus: OrderStatus) = retryWithBackoff()
        .onFailure()
        .invoke { _ ->
            ordersRepository.getOrder(orderId).flatMap { order ->
                sendOrderEvent(order.toOrderRecordWithStatus(status = orderStatus))
                order.toUni()
            }
        }

    private fun sendOrderEvent(orderRecord: Record<String, Order>) = emitter.send(orderRecord)
}