package com.arconsis.presentation.events.orders

import com.arconsis.common.Topics
import com.arconsis.common.orderTopicSerde
import com.arconsis.domain.orders.OrdersService
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrderEventsResource(
    private val ordersService: OrdersService
) {

    fun consumeOrderEvents(builder: StreamsBuilder) {
        val ordersStream = builder
            .stream(
                Topics.ORDERS.topicName,
                Consumed.with(Serdes.String(), orderTopicSerde)
            )

        ordersService.handleOrderEvents(ordersStream)
    }
}