package com.arconsis.domain.orders

import com.arconsis.data.PaymentsRepository
import com.arconsis.data.toCreatePayment
import com.arconsis.domain.payments.Payment
import com.arconsis.domain.payments.toPaymentRecord
import io.smallrye.mutiny.coroutines.awaitSuspending
import io.smallrye.reactive.messaging.MutinyEmitter
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Channel
import org.jboss.logging.Logger
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrdersService(
    @Channel("payments-out") private val emitter: MutinyEmitter<Record<String, Payment>>,
    private val paymentsRepository: PaymentsRepository,
    private val logger: Logger
) {
    suspend fun handleOrderEvents(order: Order) {
        return when (order.status) {
            OrderStatus.VALIDATED -> createPayment(order)
            else -> return
        }
    }

    private suspend fun createPayment(order: Order) {
        val payment = runCatching {
            paymentsRepository.createPayment(order.toCreatePayment())
        }.getOrElse {
            logger.error(it)
            order.toPaymentFailed()
        }
        sendPaymentEvent(payment)
    }

    private suspend fun sendPaymentEvent(payment: Payment) {
        val paymentRecord = payment.toPaymentRecord()
        logger.info("Send payment record ${paymentRecord.value()}")
        emitter.send(paymentRecord).awaitSuspending()
    }
}