package com.arconsis.domain.orders

import com.arconsis.common.REMOTE_PAYMENT_ERROR_MESSAGE
import com.arconsis.data.payments.PaymentsRepository
import com.arconsis.data.payments.toCreatePayment
import com.arconsis.data.processedevents.ProcessedEventsRepository
import com.arconsis.domain.payments.Payment
import com.arconsis.domain.payments.PaymentMessage
import com.arconsis.domain.payments.toPaymentMessageRecord
import com.arconsis.domain.processedevents.ProcessedEvent
import io.smallrye.mutiny.coroutines.awaitSuspending
import io.smallrye.reactive.messaging.MutinyEmitter
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Channel
import org.jboss.logging.Logger
import java.time.Instant
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrdersService(
    @Channel("payments-out") private val emitter: MutinyEmitter<Record<String, PaymentMessage>>,
    private val paymentsRepository: PaymentsRepository,
    private val processedEventsRepository: ProcessedEventsRepository,
    private val logger: Logger
) {
    suspend fun handleOrderEvents(orderMessage: OrderMessage) {
        val messageId = orderMessage.messageId
        val order = orderMessage.payload
        return when (order.status) {
            OrderStatus.VALIDATED -> proceedValidatedOrder(messageId, order)
            else -> return
        }
    }

    private suspend fun proceedValidatedOrder(messageId: UUID, order: Order) {
        runCatching {
            val payment = paymentsRepository.createPayment(messageId, order.toCreatePayment())
                ?: refundPayment(messageId, order)
            sendPaymentEvent(payment)
        }
            .getOrElse { err ->
                logger.error("proceedValidatedOrder failed with error: ${err.localizedMessage}")
                if (err.localizedMessage == REMOTE_PAYMENT_ERROR_MESSAGE) {
                    handleRemotePaymentError(messageId, order)
                }
            }
    }

    private suspend fun refundPayment(messageId: UUID, order: Order): Payment {
        val payment = paymentsRepository.refundPayment(messageId, order.toCreatePayment())
        return order.toPaymentFailed(payment.transactionId)
    }

    private suspend fun handleRemotePaymentError(messageId: UUID, order: Order) {
        processedEventsRepository.createEvent(ProcessedEvent(messageId, Instant.now()), null).awaitSuspending()
        sendPaymentEvent(order.toPaymentFailed(null))
    }

    private suspend fun sendPaymentEvent(payment: Payment) {
        val paymentRecord = payment.toPaymentMessageRecord()
        logger.info("Send payment record ${paymentRecord.value()}")
        emitter.send(paymentRecord).awaitSuspending()
    }
}