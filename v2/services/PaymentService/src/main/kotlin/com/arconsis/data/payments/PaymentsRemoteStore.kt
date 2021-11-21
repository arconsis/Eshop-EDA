package com.arconsis.data

import com.arconsis.domain.payments.CreatePayment
import com.arconsis.domain.payments.Payment
import com.arconsis.domain.payments.PaymentStatus
import com.arconsis.domain.payments.toPayment
import kotlinx.coroutines.delay
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PaymentsRemoteStore {
    suspend fun createPayment(createPayment: CreatePayment): Payment {
        // The transactionId is created by external API service
        val payment = createPayment.toPayment(transactionId = UUID.randomUUID(), status = PaymentStatus.SUCCESS)
        delay(5000)
        return payment
    }
}