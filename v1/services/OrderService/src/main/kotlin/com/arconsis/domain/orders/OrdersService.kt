package com.arconsis.domain.orders

import com.arconsis.data.OrdersRepository
import io.smallrye.mutiny.coroutines.awaitSuspending
import io.smallrye.reactive.messaging.MutinyEmitter
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Channel
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrdersService(
    @Channel("orders-out") private val emitter: MutinyEmitter<Record<String, Order>>,
    private val ordersRepository: OrdersRepository,
) {
    suspend fun createOrder(createOrder: CreateOrder): Order {
        val order = ordersRepository.createOrder(createOrder)
        val orderRecord = order.toOrderRecord()
        emitter.send(orderRecord).awaitSuspending()
        return order

    }

    suspend fun getOrder(orderId: UUID): Order {
        return ordersRepository.getOrder(orderId)
    }
}