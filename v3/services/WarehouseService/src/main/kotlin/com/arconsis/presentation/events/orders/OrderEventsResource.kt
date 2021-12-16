package com.arconsis.presentation.events.orders

import com.arconsis.common.Topics
import com.arconsis.domain.inventory.Inventory
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrdersService
import com.arconsis.domain.ordervalidations.OrderValidation
import com.arconsis.domain.shipments.Shipment
import io.quarkus.kafka.client.serialization.ObjectMapperSerde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.KTable
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrderEventsResource(
    private val ordersService: OrdersService
) {
    fun consumeOrderEvents(builder: StreamsBuilder, inventoryTable: KTable<String, Inventory>) {
        val orderTopicSerde = ObjectMapperSerde(Order::class.java)
        val orderValidationSerde = ObjectMapperSerde(OrderValidation::class.java)
        val shipmentTopicSerde = ObjectMapperSerde(Shipment::class.java)
        val ordersStream = builder
            .stream(
                Topics.ORDERS.topicName,
                Consumed.with(Serdes.String(), orderTopicSerde)
            )
        ordersStream.to(Topics.ORDERS_PRODUCT_KEY.topicName)
        ordersStream.handleOrderEvents(orderValidationSerde, shipmentTopicSerde, inventoryTable)
    }

    private fun KStream<String, Order>.handleOrderEvents(
        orderValidationSerde: ObjectMapperSerde<OrderValidation>,
        shipmentTopicSerde: ObjectMapperSerde<Shipment>,
        inventoryTable: KTable<String, Inventory>
    ) = ordersService.handleOrderEvents(this, orderValidationSerde, shipmentTopicSerde, inventoryTable)
}