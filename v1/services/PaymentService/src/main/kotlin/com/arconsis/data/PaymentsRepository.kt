package com.arconsis.data

import com.arconsis.domain.payments.CreatePayment
import com.arconsis.domain.payments.Payment
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PaymentsRepository(
    private val paymentsRemoteStore: PaymentsRemoteStore,
    private val paymentsDataStore: PaymentsDataStore
) {
    suspend fun createPayment(createPayment: CreatePayment): Payment {
        val payment = paymentsRemoteStore.createPayment(createPayment)

        return runCatching {
            paymentsDataStore.createPayment(payment)
        }.getOrElse { payment }
    }
}