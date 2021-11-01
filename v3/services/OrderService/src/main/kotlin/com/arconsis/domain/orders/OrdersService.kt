package com.arconsis.domain.orders

import com.arconsis.domain.events.EventsService
import com.arconsis.presentation.orders.dto.OrderCreateDto
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrdersService(val eventsService: EventsService) {

  suspend fun createOrder(orderCreateDto: OrderCreateDto): Order {
    val pendingOrder = orderCreateDto.toPendingOrder()
    val event = pendingOrder.toOrderRequestEvent()
    eventsService.sendOrderEvent(event)
    return pendingOrder
  }
}