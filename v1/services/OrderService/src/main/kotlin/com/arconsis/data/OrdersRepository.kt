package com.arconsis.data

import com.arconsis.domain.orders.CreateOrder
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrderStatus
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrdersRepository(private val sessionFactory: Mutiny.SessionFactory) {

    fun updateOrder(orderId: UUID, status: OrderStatus): Uni<Order> {

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
        }
    }

    fun createOrder(createOrder: CreateOrder): Uni<Order> {
        val orderEntity = createOrder.toOrderEntity(OrderStatus.PENDING)

        return sessionFactory.withTransaction { s, _ ->
            s.persist(orderEntity)
                .map { orderEntity.toOrder() }
        }
    }

    fun getOrder(orderId: UUID): Uni<Order> {

        return sessionFactory.withTransaction { s, _ ->
            s.find(OrderEntity::class.java, orderId)
                .map { orderEntity -> orderEntity.toOrder() }
        }
    }
}