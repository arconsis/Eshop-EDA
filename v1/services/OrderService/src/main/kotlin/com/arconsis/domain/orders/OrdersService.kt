package com.arconsis.domain.orders

import com.arconsis.common.retryWithBackoff
import com.arconsis.data.OrdersRepository
import io.smallrye.mutiny.Uni
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
    fun createOrder(createOrder: CreateOrder): Uni<Order> {
        return ordersRepository.createOrder(createOrder)
            .retryWithBackoff()
            .flatMap { order ->
                val orderRecord = order.toOrderRecord()
                emitter.send(orderRecord).map {
                    order
                }
            }
    }

    fun getOrder(orderId: UUID): Uni<Order> {
        return ordersRepository.getOrder(orderId)
    }
}