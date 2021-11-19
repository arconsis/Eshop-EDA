package com.arconsis.domain.payments

import com.arconsis.data.OrdersRepository
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrderStatus
import com.arconsis.domain.orders.toOrderRecord
import io.smallrye.mutiny.Uni
import io.smallrye.reactive.messaging.MutinyEmitter
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Channel
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
                    .flatMap { order ->
                        val orderRecord = order.toOrderRecord()
                        emitter.send(orderRecord)
                    }

            }
            PaymentStatus.FAILED -> {
                ordersRepository.updateOrder(payment.orderId, OrderStatus.PAYMENT_FAILED)
                    .flatMap { order ->
                        val orderRecord = order.toOrderRecord()
                        emitter.send(orderRecord)
                    }
            }
            else -> return Uni.createFrom().voidItem()
        }
    }
}