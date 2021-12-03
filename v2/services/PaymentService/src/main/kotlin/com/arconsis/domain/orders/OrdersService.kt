package com.arconsis.domain.orders

import com.arconsis.data.payments.PaymentsRepository
import com.arconsis.data.payments.toCreatePayment
import io.smallrye.mutiny.Uni
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrdersService(
    private val paymentsRepository: PaymentsRepository,
) {

    fun handleOrderEvents(order: Order): Uni<Void> {
        return when (order.status) {
            OrderStatus.VALIDATED -> handleValidOrder(order)
            else -> Uni.createFrom().voidItem()
        }
    }

    private fun handleValidOrder(order: Order): Uni<Void> {
        return paymentsRepository.createPayment(order.toCreatePayment())
            .map {
                null
            }
    }
}