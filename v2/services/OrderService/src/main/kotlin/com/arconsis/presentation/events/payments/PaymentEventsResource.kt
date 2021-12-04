package com.arconsis.presentation.events.payments

import com.arconsis.domain.payments.Payment
import com.arconsis.domain.payments.PaymentsService
import com.fasterxml.jackson.databind.ObjectMapper
import io.smallrye.mutiny.Uni
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Incoming
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PaymentEventsResource(
    private val paymentsService: PaymentsService,
    private val objectMapper: ObjectMapper
) {
    @Incoming("payments-in")
    fun consumePaymentEvents(paymentRecord: Record<String, PaymentEventDto>): Uni<Void> {
        val paymentEventDto = paymentRecord.value()
        val eventId = UUID.fromString(paymentEventDto.payload.currentValue.id)
        val payment = objectMapper.readValue(
            paymentEventDto.payload.currentValue.toOutboxEvent().payload,
            Payment::class.java
        )
        return paymentsService.handlePaymentEvents(eventId, payment)
            .onFailure()
            .recoverWithNull()
    }
}