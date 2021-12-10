package com.arconsis.presentation.events

import com.arconsis.presentation.events.orders.OrderEventsResource
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Produces

@ApplicationScoped
class EventsResource(
    val orderEventsResource: OrderEventsResource
) {
    @Produces
    fun createTopology(): Topology {
        val builder = StreamsBuilder()
        orderEventsResource.buildOrderEventsTopology(builder)
        return builder.build()
    }
}