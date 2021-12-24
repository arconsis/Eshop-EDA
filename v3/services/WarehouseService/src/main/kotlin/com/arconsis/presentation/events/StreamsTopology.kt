package com.arconsis.presentation.events

import com.arconsis.presentation.events.orders.OrderEventsResource
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Produces

@ApplicationScoped
class StreamsTopology(
    val orderEventsResource: OrderEventsResource,
) {

    @ApplicationScoped
    @Produces
    fun streamsBuilder(): StreamsBuilder {
        return StreamsBuilder()
    }

    @Produces
    fun createTopology(builder: StreamsBuilder): Topology {
        orderEventsResource.consumeOrderEvents(builder)
        return builder.build()
    }
}