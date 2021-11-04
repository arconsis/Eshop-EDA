package com.arconsis.data

import com.arconsis.common.OrderStatus
import com.arconsis.domain.orders.CreateOrder
import com.arconsis.domain.orders.Order
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.persistence.EntityManager

@ApplicationScoped
class OrdersRepository(private val entityManager: EntityManager) {

    fun updateOrder(orderId: UUID, status: OrderStatus): Order {
        val orderEntity = entityManager.find(OrderEntity::class.java, orderId)
        orderEntity.status = status
        val updatedEntity = entityManager.merge(orderEntity)
        entityManager.flush()
        return updatedEntity.toOrder()
    }

    fun createOrder(createOrder: CreateOrder): Order {
        val orderEntity = createOrder.toOrderEntity(OrderStatus.PENDING)
        entityManager.persist(orderEntity)
        entityManager.flush()
        return orderEntity.toOrder()
    }
}