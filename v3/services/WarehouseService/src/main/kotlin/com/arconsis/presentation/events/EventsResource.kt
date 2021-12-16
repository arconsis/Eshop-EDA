package com.arconsis.presentation.events

import com.arconsis.common.Topics
import com.arconsis.domain.inventory.Inventory
import com.arconsis.presentation.events.orders.OrderEventsResource
import io.quarkus.kafka.client.serialization.ObjectMapperSerde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.Consumed
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Produces

@ApplicationScoped
class EventsResource(
    val orderEventsResource: OrderEventsResource,
) {
    @Produces
    fun createTopology(): Topology {
        val builder = StreamsBuilder()
        val inventoryTopicSerde = ObjectMapperSerde(Inventory::class.java)
        val inventoryTable = builder.table(Topics.WAREHOUSE.topicName, Consumed.with(Serdes.String(), inventoryTopicSerde))
        orderEventsResource.consumeOrderEvents(builder, inventoryTable)
        return builder.build()
    }
}