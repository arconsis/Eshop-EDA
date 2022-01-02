package com.arconsis.domain.shipments

import com.arconsis.common.Topics
import com.arconsis.common.orderSerde
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrderStatus
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.KTable
import org.apache.kafka.streams.kstream.Produced
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ShipmentsService(private val ordersTable: KTable<String, Order>) {

    fun handleShipmentEvents(stream: KStream<String, Shipment>) {
        stream.filter { _, shipment -> shipment.isOutForShipment || shipment.isDelivered || shipment.failed }
            .join(ordersTable) { shipment, order ->
                val updatedOrder = order.copy(status = shipment.status.toOrderStatus())
                updatedOrder
            }
            .map { _, order ->
                KeyValue.pair(order.orderId.toString(), order)
            }
            .to(Topics.ORDERS.topicName, Produced.with(Serdes.String(), orderSerde))
    }

    private fun ShipmentStatus.toOrderStatus(): OrderStatus = when (this) {
        ShipmentStatus.DELIVERED -> OrderStatus.COMPLETED
        ShipmentStatus.SHIPPED -> OrderStatus.SHIPPED
        ShipmentStatus.FAILED -> OrderStatus.SHIPMENT_FAILED
    }
}