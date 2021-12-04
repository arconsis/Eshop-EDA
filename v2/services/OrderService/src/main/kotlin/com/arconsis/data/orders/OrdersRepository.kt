package com.arconsis.data.orders

import com.arconsis.domain.orders.CreateOrder
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrderStatus
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrdersRepository(private val sessionFactory: Mutiny.SessionFactory) {

    fun updateOrder(orderId: UUID, status: OrderStatus, session: Mutiny.Session?): Uni<Order> {
        return session?.let { s ->
            updateOrderWithSession(orderId, status, s)
        } ?: updateOrderWithoutSession(orderId, status)
    }

    fun createOrder(createOrder: CreateOrder, session: Mutiny.Session): Uni<Order> {
        val orderEntity = createOrder.toOrderEntity(OrderStatus.REQUESTED)
        return session.persist(orderEntity)
            .map { orderEntity.toOrder() }
    }

    fun getOrder(orderId: UUID): Uni<Order> {
        return sessionFactory.withTransaction { s, _ ->
            s.find(OrderEntity::class.java, orderId)
                .map { orderEntity -> orderEntity.toOrder() }
        }
    }

    private fun updateOrderWithSession(orderId: UUID, status: OrderStatus, session: Mutiny.Session): Uni<Order> {
        return session.find(OrderEntity::class.java, orderId)
            .map { orderEntity ->
                orderEntity.status = status
                orderEntity
            }
            .onItem().ifNotNull().transformToUni { orderEntity ->
                session.merge(orderEntity)
            }
            .map { updatedEntity -> updatedEntity.toOrder() }
    }

    private fun updateOrderWithoutSession(orderId: UUID, status: OrderStatus): Uni<Order> {
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
}