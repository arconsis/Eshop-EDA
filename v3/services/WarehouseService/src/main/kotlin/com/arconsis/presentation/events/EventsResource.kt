package com.arconsis.presentation.events

import com.arconsis.common.Topics
import com.arconsis.common.orderTopicSerde
import com.arconsis.domain.orders.OrdersService
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.Consumed
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Produces

@ApplicationScoped
class EventsResource(
    private val ordersService: OrdersService
) {

    @ApplicationScoped
    @Produces
    fun streamsBuilder(): StreamsBuilder {
        return StreamsBuilder()
    }

    @Produces
    fun createTopology(builder: StreamsBuilder): Topology {
        val ordersStream = builder
            .stream(
                Topics.ORDERS.topicName,
                Consumed.with(Serdes.String(), orderTopicSerde)
            )
        ordersService.handleOrderEvents(ordersStream)
        return builder.build()
    }
}