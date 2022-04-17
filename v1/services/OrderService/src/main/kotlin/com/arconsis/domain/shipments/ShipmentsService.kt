package com.arconsis.domain.shipments

import com.arconsis.domain.orders.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ShipmentsService(private val ordersService: OrdersService) {
    suspend fun handleShipmentEvents(shipmentMessage: ShipmentMessage) {
        val shipment = shipmentMessage.payload
        val messageId = shipmentMessage.messageId
        when (shipment.status) {
            ShipmentStatus.DELIVERED -> ordersService.updateAndSendOrder(
                messageId,
                shipment.orderId,
                OrderStatus.COMPLETED
            )
            ShipmentStatus.SHIPPED -> ordersService.updateAndSendOrder(
                messageId,
                shipment.orderId,
                OrderStatus.SHIPPED
            )
            ShipmentStatus.FAILED -> ordersService.updateAndSendOrder(
                messageId,
                shipment.orderId,
                OrderStatus.SHIPMENT_FAILED
            )
            else -> return
        }
    }
}