package com.arconsis.presentation.events.warehouse

import com.arconsis.common.LocalStores
import com.arconsis.common.Topics
import com.arconsis.common.inventoryTopicSerde
import com.arconsis.common.orderTopicSerde
import com.arconsis.domain.inventory.Inventory
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KTable
import org.apache.kafka.streams.state.KeyValueStore
import org.apache.kafka.streams.state.StoreBuilder
import org.apache.kafka.streams.state.Stores
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class WarehouseEventsResource {
    fun consumeWarehouseEvents(builder: StreamsBuilder): Pair<StoreBuilder<KeyValueStore<String, Int>>, KTable<String, Inventory>> {
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
        return Pair(reservedStock, inventoryTable)
    }
}