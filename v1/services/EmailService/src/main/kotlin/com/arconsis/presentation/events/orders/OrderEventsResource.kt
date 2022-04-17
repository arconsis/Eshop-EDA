package com.arconsis.presentation.events.orders

import com.arconsis.domain.orders.OrderMessage
import com.arconsis.domain.orders.OrdersService
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Incoming
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrderEventsResource(private val ordersService: OrdersService) {

    @Incoming("orders-in")
    suspend fun consumeOrderEvents(orderMessageRecord: Record<String, OrderMessage>) {
        val orderMessage = orderMessageRecord.value()
        return ordersService.handleOrderEvents(orderMessage)
    }
}