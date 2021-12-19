package com.arconsis.presentation.events

import com.arconsis.presentation.events.orders.OrderEventsResource
import com.arconsis.presentation.events.warehouse.WarehouseEventsResource
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Produces

@ApplicationScoped
class EventsResource(
    val orderEventsResource: OrderEventsResource,
    val warehouseEventsResource: WarehouseEventsResource,
) {
    @Produces
    fun createTopology(): Topology {
        val builder = StreamsBuilder()
        val (_, inventoryTable) = warehouseEventsResource.consumeWarehouseEvents(builder)
        orderEventsResource.consumeOrderEvents(
            builder,
            inventoryTable,
        )
        return builder.build()
    }
}