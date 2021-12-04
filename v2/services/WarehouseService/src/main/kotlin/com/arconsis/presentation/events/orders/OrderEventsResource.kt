package com.arconsis.presentation.events.orders

import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrdersService
import com.fasterxml.jackson.databind.ObjectMapper
import io.smallrye.mutiny.Uni
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Incoming
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrderEventsResource(
    private val ordersService: OrdersService,
    private val objectMapper: ObjectMapper
) {
    @Incoming("order-in")
    fun consumeOrderEvents(orderRecord: Record<String, OrderEventDto>): Uni<Void> {
        val orderEventDto = orderRecord.value()
        val eventId = UUID.fromString(orderEventDto.payload.currentValue.id)
        val order = objectMapper.readValue(orderEventDto.payload.currentValue.toOutboxEvent().payload, Order::class.java)
        return ordersService.handleOrderEvents(eventId, order)
            .onFailure()
            .recoverWithNull()
    }
}