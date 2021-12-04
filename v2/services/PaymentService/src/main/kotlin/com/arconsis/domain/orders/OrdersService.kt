package com.arconsis.domain.orders

import com.arconsis.data.payments.PaymentsRepository
import com.arconsis.data.payments.toCreatePayment
import io.smallrye.mutiny.Uni
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrdersService(
    private val paymentsRepository: PaymentsRepository,
) {

    fun handleOrderEvents(eventId: UUID,  order: Order): Uni<Void> {
        return when (order.status) {
            OrderStatus.VALIDATED -> handleValidOrder(eventId, order)
            else -> Uni.createFrom().voidItem()
        }
    }

    private fun handleValidOrder(eventId: UUID, order: Order): Uni<Void> {
        return paymentsRepository.createPayment(eventId, order.toCreatePayment())
            .map {
                null
            }
    }
}