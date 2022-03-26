package com.arconsis.data

import com.arconsis.domain.orders.CreateOrder
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrderStatus
import io.smallrye.mutiny.coroutines.awaitSuspending
import org.hibernate.reactive.mutiny.Mutiny
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrdersRepository(private val sessionFactory: Mutiny.SessionFactory) {

    suspend fun updateOrder(orderId: UUID, status: OrderStatus): Order {

        return sessionFactory.withTransaction { s, _ ->
            s.find(OrderEntity::class.java, orderId)
                .map { orderEntity ->
                    orderEntity.status = status
                    orderEntity
                }
                .onItem().ifNotNull().transformToUni { orderEntity ->
                    s.merge(orderEntity)
                }
                .map { updatedEntity -> updatedEntity.toOrder() }
        }.awaitSuspending()
    }

    suspend fun createOrder(createOrder: CreateOrder): Order {
        val orderEntity = createOrder.toOrderEntity(OrderStatus.REQUESTED)

        return sessionFactory.withTransaction { s, _ ->
            s.persist(orderEntity)
                .map { orderEntity.toOrder() }
        }.awaitSuspending()
    }

    suspend fun getOrder(orderId: UUID): Order {

        return sessionFactory.withTransaction { s, _ ->
            s.find(OrderEntity::class.java, orderId)
                .map { orderEntity -> orderEntity.toOrder() }
        }.awaitSuspending()
    }
}