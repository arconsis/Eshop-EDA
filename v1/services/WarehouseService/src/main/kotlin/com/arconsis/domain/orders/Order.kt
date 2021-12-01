package com.arconsis.domain.orders

import com.arconsis.domain.shipments.Shipment
import com.arconsis.domain.shipments.ShipmentStatus
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer
import java.util.*

data class Order(
    val id: UUID,
    val userId: UUID,
    val amount: Double,
    val currency: String,
    val productId: String,
    val quantity: Int,
    val status: OrderStatus,
)

enum class OrderStatus {
    REQUESTED,
    VALIDATED,
    OUT_OF_STOCK,
    PAID,
    SHIPPED,
    COMPLETED,
    PAYMENT_FAILED,
    CANCELLED,
    REFUNDED,
    SHIPMENT_FAILED
}

fun Order.toFailedShipment(shipmentId: UUID?) = Shipment(
    id = shipmentId,
    orderId = id,
    userId = userId,
    status = ShipmentStatus.FAILED
)

class OrdersDeserializer : ObjectMapperDeserializer<Order>(Order::class.java)