package com.arconsis.presentation.events.orders

import com.arconsis.common.Topics
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrdersService
import com.arconsis.domain.ordervalidations.OrderValidation
import com.arconsis.domain.shipments.Shipment
import io.quarkus.kafka.client.serialization.ObjectMapperSerde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KStream
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrderEventsResource(
    private val ordersService: OrdersService
) {
    fun consumeOrderEvents(builder: StreamsBuilder) {
        val orderTopicSerde = ObjectMapperSerde(Order::class.java)
        val orderValidationSerde = ObjectMapperSerde(OrderValidation::class.java)
        val shipmentTopicSerde = ObjectMapperSerde(Shipment::class.java)
        builder
            .stream(
                Topics.ORDERS.topicName,
                Consumed.with(Serdes.String(), orderTopicSerde)
            )
            .handleOrderEvents(orderValidationSerde, shipmentTopicSerde)
    }

    private fun KStream<String, Order>.handleOrderEvents(
        orderValidationSerde: ObjectMapperSerde<OrderValidation>,
        shipmentTopicSerde: ObjectMapperSerde<Shipment>
    ) = ordersService.handleOrderEvents(this, orderValidationSerde, shipmentTopicSerde)
}