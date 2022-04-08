package com.arconsis.data

import com.arconsis.domain.payments.CreatePayment
import com.arconsis.domain.payments.Payment
import com.arconsis.domain.payments.PaymentStatus
import com.arconsis.domain.payments.toPayment
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.coroutines.awaitSuspending
import java.time.Duration
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PaymentsRemoteStore {
    suspend fun createPayment(createPayment: CreatePayment): Payment {
        // The transactionId is created by external API service
        val payment = createPayment.toPayment(transactionId = UUID.randomUUID(), status = PaymentStatus.SUCCEED)
        return Uni.createFrom().item(payment).onItem().delayIt().by(Duration.ofMillis(5000)).awaitSuspending()
        //return Uni.createFrom().failure(Exception("test"))
    }
}