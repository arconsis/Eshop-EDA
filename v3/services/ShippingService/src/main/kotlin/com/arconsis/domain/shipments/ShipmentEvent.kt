package com.arconsis.domain.shipments

import com.arconsis.domain.orders.Order
import java.util.*

data class ShipmentEvent(
	val key: String,
	val value: Shipment,
)

fun Order.toShipmentEvent(status: ShipmentStatus) = ShipmentEvent(
	key = orderId.toString(),
	value = Shipment(
		orderId = orderId,
		shipmentId = UUID.randomUUID(),
		userId = userId,
		status = status
	)
)