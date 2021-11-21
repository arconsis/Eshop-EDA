package com.arconsis.presentation.events.payments

import com.arconsis.domain.payments.Payment
import com.arconsis.domain.payments.PaymentsService
import io.smallrye.mutiny.Uni
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Incoming
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PaymentEventsResource(private val paymentsService: PaymentsService) {

    @Incoming("payments-in")
    suspend fun consumePaymentEvents(paymentRecord: Record<String, Payment>) {
        val payment = paymentRecord.value()
        return paymentsService.handlePaymentEvents(payment)
    }
}