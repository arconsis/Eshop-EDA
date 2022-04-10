package com.arconsis.data

import com.arconsis.domain.payments.CreatePayment
import com.arconsis.domain.payments.Payment
import org.jboss.logging.Logger
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PaymentsRepository(
    private val paymentsRemoteStore: PaymentsRemoteStore,
    private val paymentsDataStore: PaymentsDataStore,
    private val logger: Logger
) {
    suspend fun createPayment(createPayment: CreatePayment): Payment {
        val payment = paymentsRemoteStore.createPayment(createPayment)

        return runCatching {
            paymentsDataStore.createPayment(payment)
        }.getOrElse {
            logger.error(it)
            payment
        }
    }
}