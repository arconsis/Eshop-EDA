package com.arconsis.data.payments

import com.arconsis.data.outboxevents.OutboxEventsRepository
import com.arconsis.data.processedevents.ProcessedEventsRepository
import com.arconsis.domain.outboxevents.OutboxEvent
import com.arconsis.domain.payments.*
import com.arconsis.domain.processedevents.ProcessedEvent
import com.fasterxml.jackson.databind.ObjectMapper
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import java.time.Duration
import java.time.Instant
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PaymentsRepository(
    private val paymentsRemoteStore: PaymentsRemoteStore,
    private val paymentsDataStore: PaymentsDataStore,
    private val outboxEventsRepository: OutboxEventsRepository,
    private val processedEventsRepository: ProcessedEventsRepository,
    private val sessionFactory: Mutiny.SessionFactory,
    private val objectMapper: ObjectMapper,
) {
    fun createPayment(eventId: UUID, createPayment: CreatePayment): Uni<Payment> {
        // https://github.com/quarkusio/quarkus/issues/23804
        val payment = createPayment.toPayment(transactionId = UUID.randomUUID(), status = PaymentStatus.SUCCEED)
        return sessionFactory.withTransaction { session, _ ->
            processedEventsRepository.getEvent(eventId, session)
                .createPaymentEntity(payment, session)
                .createOutboxEvent(session)
                .createProceedEvent(eventId, session)
                .map {
                    payment
                }
        }
    }

    private fun Uni<ProcessedEvent?>.createPaymentEntity(payment: Payment, session: Mutiny.Session) = flatMap { event ->
        if (event != null) Uni.createFrom().voidItem()
        paymentsDataStore.createPayment(payment, session)
    }

    private fun Uni<Payment>.createOutboxEvent(session: Mutiny.Session) = flatMap { payment ->
        val createOutboxEvent = payment.toCreateOutboxEvent(objectMapper)
        outboxEventsRepository.createEvent(createOutboxEvent, session)
    }

    private fun Uni<OutboxEvent>.createProceedEvent(eventId: UUID, session: Mutiny.Session) =
        flatMap {
            val proceedEvent = ProcessedEvent(
                eventId = eventId,
                processedAt = Instant.now()
            )
            processedEventsRepository.createEvent(proceedEvent, session)
        }
}