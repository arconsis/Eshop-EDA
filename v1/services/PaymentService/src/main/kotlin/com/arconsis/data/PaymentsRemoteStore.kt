package com.arconsis.data

import com.arconsis.domain.payments.CreatePayment
import com.arconsis.domain.payments.Payment
import com.arconsis.domain.payments.PaymentStatus
import com.arconsis.domain.payments.toPayment
import io.smallrye.mutiny.Uni
import java.time.Duration
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PaymentsRemoteStore {
    fun createPayment(createPayment: CreatePayment): Uni<Payment> {
        // The transactionId is created by external API service
        val payment = createPayment.toPayment(transactionId = UUID.randomUUID(), status = PaymentStatus.SUCCESS)
        return Uni.createFrom().item(payment).onItem().delayIt().by(Duration.ofMillis(5000))
    }

    fun refundPayment(createPayment: CreatePayment): Uni<Payment> {
        // The transactionId is created by external API service
        val payment = createPayment.toPayment(transactionId = UUID.randomUUID(), status = PaymentStatus.REFUNDED)
        return Uni.createFrom().item(payment).onItem().delayIt().by(Duration.ofMillis(5000))
    }
}