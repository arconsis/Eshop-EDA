package com.arconsis.presentation.events.orders

import com.arconsis.common.Topics
import com.arconsis.common.orderTopicSerde
import com.arconsis.domain.inventory.Inventory
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrdersService
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
    fun consumeOrderEvents(
        builder: StreamsBuilder,
        inventoryTable: KTable<String, Inventory>,
    ) {
        val ordersStream = builder
            .stream(
                Topics.ORDERS.topicName,
                Consumed.with(Serdes.String(), orderTopicSerde)
            )
        ordersStream.handleOrderEvents(inventoryTable)
    }

    private fun KStream<String, Order>.handleOrderEvents(inventoryTable: KTable<String, Inventory>) =
        ordersService.handleOrderEvents(
            this,
            inventoryTable,
        )
}