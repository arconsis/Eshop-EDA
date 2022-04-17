package com.arconsis.data.payments

import com.arconsis.domain.payments.Payment
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PaymentsDataStore {
    fun createPayment(payment: Payment, session: Mutiny.Session): Uni<Payment> {
        val paymentEntity = payment.toPaymentEntity()
        return session.persist(paymentEntity)
            .map {
                paymentEntity.toPayment()
            }
    }
}