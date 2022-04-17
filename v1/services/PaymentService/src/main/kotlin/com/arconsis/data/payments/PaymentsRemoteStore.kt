package com.arconsis.data.payments

import com.arconsis.common.REMOTE_PAYMENT_ERROR_MESSAGE
import com.arconsis.domain.payments.CreatePayment
import com.arconsis.domain.payments.Payment
import com.arconsis.domain.payments.PaymentStatus
import com.arconsis.domain.payments.toPayment
import io.smallrye.mutiny.Uni
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PaymentsRemoteStore {
    fun createPayment(createPayment: CreatePayment): Uni<Payment> {
        // The transactionId is created by external API service
        val payment = createPayment.toPayment(transactionId = UUID.randomUUID(), status = PaymentStatus.SUCCEED)
        return Uni.createFrom().item(payment)
        // return Uni.createFrom().failure(Exception(REMOTE_PAYMENT_ERROR_MESSAGE))
    }

    fun refundPayment(createPayment: CreatePayment): Uni<Payment> {
        // The transactionId is created by external API service
        val payment = createPayment.toPayment(transactionId = UUID.randomUUID(), status = PaymentStatus.REFUNDED)
        return Uni.createFrom().item(payment)
        //return Uni.createFrom().failure(Exception(REMOTE_PAYMENT_ERROR_MESSAGE))
    }
}