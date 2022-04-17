package com.arconsis.domain.payments

import com.arconsis.domain.orders.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PaymentsService(private val ordersService: OrdersService) {
    suspend fun handlePaymentEvents(paymentMessage: PaymentMessage) {
        val messageId = paymentMessage.messageId
        val payment = paymentMessage.payload
        when (payment.status) {
            PaymentStatus.SUCCEED -> ordersService.updateAndSendOrder(
                messageId,
                payment.orderId,
                OrderStatus.PAID
            )
            PaymentStatus.FAILED -> ordersService.updateAndSendOrder(
                messageId,
                payment.orderId,
                OrderStatus.PAYMENT_FAILED
            )
            else -> return
        }
    }
}