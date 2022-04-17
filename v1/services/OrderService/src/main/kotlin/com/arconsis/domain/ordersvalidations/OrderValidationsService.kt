package com.arconsis.domain.ordersvalidations

import com.arconsis.domain.orders.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrderValidationsService(private val ordersService: OrdersService) {

    suspend fun handleOrderValidationEvents(orderValidationMessage: OrderValidationMessage) {
        val messageId = orderValidationMessage.messageId
        val orderValidation = orderValidationMessage.payload
        when (orderValidation.status) {
            OrderValidationStatus.VALIDATED -> ordersService.updateAndSendOrder(
                messageId,
                orderValidation.orderId,
                OrderStatus.VALIDATED
            )
            OrderValidationStatus.INVALID -> ordersService.updateAndSendOrder(
                messageId,
                orderValidation.orderId,
                OrderStatus.OUT_OF_STOCK
            )
        }
    }
}