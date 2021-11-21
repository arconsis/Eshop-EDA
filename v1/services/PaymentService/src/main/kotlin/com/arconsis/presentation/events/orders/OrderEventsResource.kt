package com.arconsis.presentation.events.orders

import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrdersService
import io.smallrye.mutiny.Uni
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Incoming
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrderEventsResource(
    private val ordersService: OrdersService,
) {
    @Incoming("orders-in")
    fun consumeOrderEvents(orderRecord: Record<String, Order>): Uni<Void> {
        val order = orderRecord.value()
        return ordersService.handleOrderEvents(order)
            .onFailure()
            .recoverWithNull()
    }
}