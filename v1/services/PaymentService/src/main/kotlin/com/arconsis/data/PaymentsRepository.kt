package com.arconsis.data

import com.arconsis.domain.payments.CreatePayment
import com.arconsis.domain.payments.Payment
import javax.enterprise.context.ApplicationScoped
import javax.persistence.EntityManager

@ApplicationScoped
class PaymentsRepository(private val entityManager: EntityManager) {

    fun createPayment(payment: CreatePayment): Payment? {
        return try {
            val paymentEntity = payment.toPaymentEntity()
            entityManager.persist(paymentEntity)
            entityManager.flush()
            return paymentEntity.toPayment()
        } catch (e: Exception) {
            null
        }
    }
}