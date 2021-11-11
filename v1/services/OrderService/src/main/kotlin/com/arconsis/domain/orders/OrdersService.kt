package com.arconsis.domain.orders

import com.arconsis.data.OrdersRepository
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class OrdersService(
    private val ordersRepository: OrdersRepository,
) {

    @Transactional
    fun getOrder(orderId: UUID): Order {
        return ordersRepository.getUser(orderId)
    }
}