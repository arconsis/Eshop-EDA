package com.arconsis.presentation.events.warehouse

import com.arconsis.common.LocalStores
import com.arconsis.common.Topics
import com.arconsis.common.inventoryTopicSerde
import com.arconsis.domain.inventory.Inventory
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KTable
import org.apache.kafka.streams.state.Stores
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Produces

@ApplicationScoped
class WarehouseEventsResource {

    @Produces
    fun consumeWarehouseEvents(builder: StreamsBuilder): KTable<String, Inventory> {
        val changelogConfig: HashMap<String, String> = HashMap()
        val reservedStockStoreBuilder = Stores
            .keyValueStoreBuilder(
                Stores.persistentKeyValueStore(LocalStores.RESERVED_STOCK.storeName),
                Serdes.String(),
                Serdes.Integer()
            )
            .withLoggingEnabled(changelogConfig)
        builder.addStateStore(reservedStockStoreBuilder)

        return builder.table(
            Topics.WAREHOUSE.topicName,
            Consumed.with(
                Serdes.String(),
                inventoryTopicSerde
            )
        )
    }
}