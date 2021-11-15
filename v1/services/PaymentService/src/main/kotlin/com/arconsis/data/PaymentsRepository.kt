package com.arconsis.data

import com.arconsis.domain.payments.CreatePayment
import com.arconsis.domain.payments.Payment
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PaymentsRepository(private val sessionFactory: Mutiny.SessionFactory) {

    fun createPayment(payment: CreatePayment): Uni<Payment> {
        val paymentEntity = payment.toPaymentEntity()
        return sessionFactory.withTransaction { s, _ ->
            s.persist(paymentEntity)
                .map { paymentEntity.toPayment() }
        }
    }
}