package com.arconsis.domain.shipments

import com.arconsis.common.Topics
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrderStatus
import io.quarkus.kafka.client.serialization.ObjectMapperSerde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.KTable
import org.apache.kafka.streams.kstream.Produced
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ShipmentsService {
    fun handleShipmentEvents(
        stream: KStream<String, Shipment>,
        ordersTable: KTable<String, Order>,
        orderSerde: ObjectMapperSerde<Order>
    ) = stream.filter { _, shipment -> shipment.isOutForShipment }
        .join(ordersTable) { _, order ->
            order
        }
        .map { _, orderValidation ->
            val outForShipmentOrder = orderValidation.copy(status = OrderStatus.SHIPPED)
            KeyValue.pair(outForShipmentOrder.userId.toString(), outForShipmentOrder)
        }
        .to(Topics.ORDERS.topicName, Produced.with(Serdes.String(), orderSerde))
}