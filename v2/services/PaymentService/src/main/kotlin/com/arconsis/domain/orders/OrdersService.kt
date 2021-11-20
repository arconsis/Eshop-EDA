package com.arconsis.domain.orders

import com.arconsis.data.PaymentsRepository
import com.arconsis.data.toCreatePayment
import com.arconsis.domain.payments.Payment
import com.arconsis.domain.payments.PaymentStatus
import com.arconsis.domain.payments.toPaymentRecord
import io.smallrye.mutiny.Uni
import io.smallrye.reactive.messaging.MutinyEmitter
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Channel
import java.time.Duration
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrdersService(
    @Channel("payments-out") private val emitter: MutinyEmitter<Record<String, Payment>>,
    private val paymentsRepository: PaymentsRepository,
) {
    fun handleOrderEvents(order: Order): Uni<Void> {
        return when (order.status) {
            OrderStatus.VALID -> {
                // TODO: simulate API call
                val createPaymentDto = order.toCreatePayment(PaymentStatus.SUCCESS)
                paymentsRepository.createPayment(createPaymentDto)
                    .onItem().delayIt().by(Duration.ofMillis(5000))
                    .flatMap { payment ->
                        val paymentRecord = payment.toPaymentRecord()
                        emitter.send(paymentRecord)
                    }
            }
            else -> Uni.createFrom().voidItem()
        }
    }
}