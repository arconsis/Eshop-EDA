package com.arconsis.data.payments

import com.arconsis.domain.payments.CreatePayment
import com.arconsis.domain.payments.Payment
import io.smallrye.mutiny.Uni
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
            }
    }
}