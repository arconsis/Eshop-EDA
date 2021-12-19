package com.arconsis.presentation.events

import com.arconsis.common.LocalStores
import com.arconsis.common.Topics
import com.arconsis.common.inventoryTopicSerde
import com.arconsis.domain.inventory.Inventory
import com.arconsis.presentation.events.orders.OrderEventsResource
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KTable
import org.apache.kafka.streams.state.KeyValueStore
import org.apache.kafka.streams.state.StoreBuilder
import org.apache.kafka.streams.state.Stores
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Produces


@ApplicationScoped
class EventsResource(
    val orderEventsResource: OrderEventsResource,
) {
    @Produces
    fun createTopology(): Topology {
        val builder = StreamsBuilder()
        val changelogConfig: HashMap<String, String> = HashMap()
        val reservedStock: StoreBuilder<KeyValueStore<String, Int>> = Stores
            .keyValueStoreBuilder(
                Stores.persistentKeyValueStore(LocalStores.RESERVED_STOCK.storeName),
                Serdes.String(),
                Serdes.Integer()
            )
            .withLoggingEnabled(changelogConfig)
        builder.addStateStore(reservedStock)
        val inventoryTable: KTable<String, Inventory> = builder.table(
            Topics.WAREHOUSE.topicName,
            Consumed.with(
                Serdes.String(),
                inventoryTopicSerde
            )
        )

        orderEventsResource.consumeOrderEvents(
            builder,
            inventoryTable,
        )
        return builder.build()
    }
}