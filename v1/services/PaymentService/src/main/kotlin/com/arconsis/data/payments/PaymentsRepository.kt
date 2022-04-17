package com.arconsis.data.payments

import com.arconsis.data.processedevents.ProcessedEventsRepository
import com.arconsis.domain.payments.CreatePayment
import com.arconsis.domain.payments.Payment
import com.arconsis.domain.processedevents.ProcessedEvent
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.coroutines.awaitSuspending
import org.hibernate.reactive.mutiny.Mutiny
import org.jboss.logging.Logger
import java.time.Instant
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PaymentsRepository(
    private val paymentsRemoteStore: PaymentsRemoteStore,
    private val paymentsDataStore: PaymentsDataStore,
    private val processedEventsRepository: ProcessedEventsRepository,
    private val sessionFactory: Mutiny.SessionFactory,
    private val logger: Logger
) {
    suspend fun createPayment(eventId: UUID, createPayment: CreatePayment): Payment? {
        return paymentsRemoteStore.createPayment(createPayment)
            .storePayment(eventId)
            .awaitSuspending()
    }

    suspend fun refundPayment(eventId: UUID, createPayment: CreatePayment): Payment {
        return paymentsRemoteStore.refundPayment(createPayment)
            .storePayment(eventId)
            .awaitSuspending()
    }

    private fun Uni<Payment>.storePayment(
        eventId: UUID,
        errorMessage: String = "Store payment failed and rolled back"
    ) = flatMap { remotePayment ->
        sessionFactory.withTransaction { session, _ ->
            processedEventsRepository.createEvent(ProcessedEvent(eventId, Instant.now()), session)
                .flatMap {
                    paymentsDataStore.createPayment(remotePayment, session)
                }
        }.onFailure()
            .recoverWithItem { e ->
                logger.error("$errorMessage because of error: $e")
                null
            }
    }
}