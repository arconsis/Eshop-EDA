package com.arconsis.domain.orders

import com.arconsis.data.OrdersRepository
import io.smallrye.mutiny.Uni
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrdersService(
    private val ordersRepository: OrdersRepository,
) {

    fun getOrder(orderId: UUID): Uni<Order> {
        return ordersRepository.getOrder(orderId)
    }
}