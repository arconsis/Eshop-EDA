package com.arconsis.presentation.events.payments

import com.arconsis.domain.payments.PaymentMessage
import com.arconsis.domain.payments.PaymentsService
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Incoming
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PaymentEventsResource(private val paymentsService: PaymentsService) {

    @Incoming("payments-in")
    suspend fun consumePaymentEvents(paymentMessageRecord: Record<String, PaymentMessage>) {
        // TODO: Log the possible error here
        val paymentMessage = paymentMessageRecord.value()
        runCatching {
            paymentsService.handlePaymentEvents(paymentMessage)
        }.getOrNull()
    }
}