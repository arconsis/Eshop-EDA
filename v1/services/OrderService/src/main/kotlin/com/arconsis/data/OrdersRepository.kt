package com.arconsis.data

import com.arconsis.domain.orders.CreateOrder
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrderStatus
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.persistence.EntityManager

@ApplicationScoped
class OrdersRepository(private val entityManager: EntityManager) {

    fun updateOrder(orderId: UUID, status: OrderStatus): Order? {
        val orderEntity = entityManager.find(OrderEntity::class.java, orderId)

        // TODO: Handle error in kafka
        if (orderEntity == null) {
            return null
        }

        orderEntity.status = status
        return try {
            val updatedEntity = entityManager.merge(orderEntity)
            entityManager.flush()
            updatedEntity.toOrder()
        } catch (e: Exception) {
            null
        }
    }

    fun createOrder(createOrder: CreateOrder): Order {
        val orderEntity = createOrder.toOrderEntity(OrderStatus.PENDING)
        entityManager.persist(orderEntity)
        entityManager.flush()
        return orderEntity.toOrder()
    }
}