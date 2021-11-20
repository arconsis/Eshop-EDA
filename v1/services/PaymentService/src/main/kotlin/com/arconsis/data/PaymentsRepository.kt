package com.arconsis.data

import com.arconsis.common.retryWithBackoff
import com.arconsis.domain.payments.CreatePayment
import com.arconsis.domain.payments.Payment
import io.smallrye.mutiny.Uni
import java.time.Duration
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PaymentsRepository(
    private val paymentsRemoteStore: PaymentsRemoteStore,
    private val paymentsDataStore: PaymentsDataStore
) {
    fun createPayment(createPayment: CreatePayment): Uni<Payment> {
        return paymentsRemoteStore.createPayment(createPayment)
            .flatMap { payment ->
                paymentsDataStore.createPayment(payment)
                    .handleCreatePaymentError(payment)
            }
    }

    fun refundPayment(createPayment: CreatePayment): Uni<Payment> {
        return paymentsRemoteStore.refundPayment(createPayment)
            .flatMap { payment ->
                if (payment == null) {
                    throw Exception("Payment failed")
                }
                paymentsDataStore.createPayment(payment)
            }
            .onItem()
            .failWith { _ -> throw Exception("Payment failed") }
    }

    private fun Uni<Payment>.handleCreatePaymentError(payment: Payment) = retryWithBackoff()
        .onFailure()
        .recoverWithItem(payment)
}