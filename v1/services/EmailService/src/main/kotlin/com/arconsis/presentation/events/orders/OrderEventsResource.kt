package com.arconsis.presentation.events.orders

import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrdersService
import io.smallrye.reactive.messaging.annotations.Blocking
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Incoming
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrderEventsResource(private val ordersService: OrdersService) {
    @Blocking
    @Incoming("orders-in")
    fun consumeOrderEvents(orderRecord: Record<String, Order>) {
        val order = orderRecord.value()
        return ordersService.handleOrderEvents(order)
    }
}