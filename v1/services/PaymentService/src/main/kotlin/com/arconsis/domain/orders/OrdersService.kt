package com.arconsis.domain.orders

import com.arconsis.domain.payments.PaymentService
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrdersService(private val paymentService: PaymentService) {
    suspend fun handleOrderEvents(orderMessage: OrderMessage) {
        val messageId = orderMessage.messageId
        val order = orderMessage.payload
        return when (order.status) {
            OrderStatus.VALIDATED -> paymentService.payOrder(messageId, order)
            else -> return
        }
    }
}