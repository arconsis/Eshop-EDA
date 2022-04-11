package com.arconsis.data

import com.arconsis.domain.payments.Payment
import io.smallrye.mutiny.coroutines.awaitSuspending
import org.hibernate.reactive.mutiny.Mutiny
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PaymentsDataStore(private val sessionFactory: Mutiny.SessionFactory) {

    suspend fun createPayment(payment: Payment): Payment {
        val paymentEntity = payment.toPaymentEntity()
        return sessionFactory.withTransaction { s, _ ->
            s.persist(paymentEntity)
                .map { paymentEntity.toPayment() }
        }.awaitSuspending()
    }
}