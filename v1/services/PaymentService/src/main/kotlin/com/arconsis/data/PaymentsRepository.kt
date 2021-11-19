package com.arconsis.data

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
                if (payment == null) {
                    throw Exception("Payment failed")
                }
                paymentsDataStore.createPayment(payment)
                    .onFailure()
                    .recoverWithUni { _ ->
                        refundPayment(createPayment)
                            .onItem().failWith { _ ->
                                throw Exception("Payment failed")
                            }
                    }
            }
    }

    fun refundPayment(createPayment: CreatePayment): Uni<Payment> {
        return paymentsRemoteStore.refundPayment(createPayment)
            .onItem().failWith { _ ->
                throw Exception("Payment failed")
            }
            .flatMap { payment ->
                if (payment == null) {
                    throw Exception("Payment failed")
                }
                paymentsDataStore.createPayment(payment)
            }
    }
}