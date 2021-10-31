package com.arconsis.domain.orders

import com.arconsis.presentation.orders.dto.OrderCreateDto
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrdersService(val kafkaService: KafkaService) {

    fun createOrder(orderCreateDto: OrderCreateDto): Order {

        val orderNo = UUID.randomUUID()
        val pendingOrder = orderCreateDto.toPendingOrder(orderNo)
        val event = creatOrderRequestEventPair(
            pendingOrder,
        )

        kafkaService.sendOrderEvent(event)
        return pendingOrder
    }
}