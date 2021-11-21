package com.arconsis.data

import com.arconsis.domain.payments.CreatePayment
import com.arconsis.domain.payments.Payment
import io.smallrye.mutiny.coroutines.awaitSuspending
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PaymentsRepository(
    private val paymentsRemoteStore: PaymentsRemoteStore,
    private val paymentsDataStore: PaymentsDataStore
) {
    suspend fun createPayment(createPayment: CreatePayment): Payment {
        val payment = paymentsRemoteStore.createPayment(createPayment)
        paymentsDataStore.createPayment(payment).awaitSuspending()
        return payment
    }
}