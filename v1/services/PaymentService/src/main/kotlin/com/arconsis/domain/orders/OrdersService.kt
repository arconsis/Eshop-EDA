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
                    .onFailure()
                    .invoke { _ ->
                        val payment = order.toPaymentFailed()
                        val paymentRecord = payment.toPaymentRecord()
                        emitter.send(paymentRecord)
                    }
                    .flatMap { payment ->
                        val paymentRecord = payment.toPaymentRecord()
                        emitter.send(paymentRecord)
                    }
                    .onFailure()
                    .recoverWithNull()
            }
            OrderStatus.SHIPMENT_FAILED -> {
                paymentsRepository.refundPayment(order.toCreatePayment())
                    .flatMap { Uni.createFrom().voidItem() }
                    .onFailure()
                    .recoverWithNull()
            }
            else -> Uni.createFrom().voidItem()
        }
    }
}