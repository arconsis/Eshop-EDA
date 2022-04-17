package com.arconsis.domain.orders

import com.arconsis.domain.inventory.InventoryService
import com.arconsis.domain.shipments.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrdersService(
    private val inventoryService: InventoryService,
    private val shipmentsService: ShipmentsService
) {
    suspend fun handleOrderEvents(orderMessage: OrderMessage) {
        val order = orderMessage.payload
        val messageId = orderMessage.messageId
        return when (order.status) {
            OrderStatus.REQUESTED -> inventoryService.proceedRequestedOrder(messageId, order)
            OrderStatus.PAID -> shipmentsService.proceedPaidOrder(messageId, order)
            OrderStatus.PAYMENT_FAILED -> inventoryService.proceedFailedPaymentOrder(messageId, order)
            else -> return
        }
    }
}