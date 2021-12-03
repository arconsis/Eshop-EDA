package com.arconsis.data.payments

import com.arconsis.data.outboxevents.OutboxEventsRepository
import com.arconsis.domain.orders.OrderStatus
import com.arconsis.domain.payments.CreatePayment
import com.arconsis.domain.payments.Payment
import com.arconsis.domain.payments.toCreateOutboxEvent
import com.fasterxml.jackson.databind.ObjectMapper
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PaymentsRepository(
    private val paymentsRemoteStore: PaymentsRemoteStore,
    private val paymentsDataStore: PaymentsDataStore,
    private val outboxEventsRepository: OutboxEventsRepository,
    private val sessionFactory: Mutiny.SessionFactory,
    private val objectMapper: ObjectMapper,
) {
    fun createPayment(createPayment: CreatePayment): Uni<Payment> {
        return paymentsRemoteStore.createPayment(createPayment)
            .flatMap { payment ->
                sessionFactory.withTransaction { session, _ ->
                    paymentsDataStore.createPayment(payment, session)
                        .flatMap { payment ->
                            val createOutboxEvent = payment.toCreateOutboxEvent(objectMapper)
                            outboxEventsRepository.createEvent(createOutboxEvent, session)
                        }
                        .map {
                            payment
                        }
                }
            }
    }
}