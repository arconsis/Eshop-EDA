package com.arconsis.domain.orders

import com.arconsis.data.PaymentsRepository
import com.arconsis.data.toCreatePayment
import com.arconsis.domain.payments.Payment
import com.arconsis.domain.payments.toPaymentRecord
import io.smallrye.mutiny.Uni
import io.smallrye.reactive.messaging.MutinyEmitter
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Channel
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrdersService(
    @Channel("payments-out") private val emitter: MutinyEmitter<Record<String, Payment>>,
    private val paymentsRepository: PaymentsRepository,
) {
    fun handleOrderEvents(order: Order): Uni<Void> {
        return when (order.status) {
            OrderStatus.VALID -> {
                paymentsRepository.createPayment(order.toCreatePayment())
                    .handleCreatePaymentError(order)
                    .sendPaymentEvent()
            }
            else -> Uni.createFrom().voidItem()
        }
    }

    private fun Uni<Payment>.handleCreatePaymentError(order: Order) = onFailure()
        .invoke { _ ->
            val payment = order.toPaymentFailed()
            val paymentRecord = payment.toPaymentRecord()
            sendPaymentEvent(paymentRecord)
        }

    private fun Uni<Payment>.sendPaymentEvent() = flatMap { payment ->
        val paymentRecord = payment.toPaymentRecord()
        sendPaymentEvent(paymentRecord)
    }

    private fun sendPaymentEvent(paymentRecord: Record<String, Payment>) = emitter.send(paymentRecord)
}