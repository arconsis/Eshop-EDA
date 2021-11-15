package com.arconsis.domain.events

import com.arconsis.data.PaymentsRepository
import com.arconsis.data.toCreatePayment
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrderStatus
import com.arconsis.domain.payments.Payment
import com.arconsis.domain.payments.PaymentStatus
import com.arconsis.domain.payments.toPaymentRecord
import io.smallrye.mutiny.Uni
import io.smallrye.reactive.messaging.MutinyEmitter
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Channel
import org.eclipse.microprofile.reactive.messaging.Incoming
import java.time.Duration
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class EventsService(
    @Channel("payments-out") private val emitter: MutinyEmitter<Record<String, Payment>>,
    private val paymentsRepository: PaymentsRepository,
) {
    @Incoming("orders-in")
    fun consumeOrderEvents(orderRecord: Record<String, Order>): Uni<Void> {
        val value = orderRecord.value()
        return when (value.status) {
            OrderStatus.VALID -> {
                // TODO: simulate API call
                val createPaymentDto = value.toCreatePayment(PaymentStatus.SUCCESS)
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