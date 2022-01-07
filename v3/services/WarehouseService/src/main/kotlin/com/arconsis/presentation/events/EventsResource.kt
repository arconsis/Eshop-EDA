package com.arconsis.presentation.events

import com.arconsis.common.LocalStores
import com.arconsis.common.Topics
import com.arconsis.common.inventoryTopicSerde
import com.arconsis.common.orderTopicSerde
import com.arconsis.domain.inventory.Inventory
import com.arconsis.domain.orders.OrdersService
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KTable
import org.apache.kafka.streams.state.Stores
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Produces

@ApplicationScoped
class EventsResource(
    private val ordersService: OrdersService
) {

    @Produces
    fun createTopology(): Topology {
        val builder = StreamsBuilder()


        createReservedStockStateStore(builder)
        val inventoryTable = createInventoryKTable(builder)

        val ordersStream = builder
            .stream(
                Topics.ORDERS.topicName,
                Consumed.with(Serdes.String(), orderTopicSerde)
            )
        ordersService.handleOrderEvents(ordersStream, inventoryTable)
        return builder.build()
    }

    private fun createInventoryKTable(builder: StreamsBuilder): KTable<String, Inventory> {

        return builder.table(
            Topics.WAREHOUSE.topicName,
            Consumed.with(
                Serdes.String(),
                inventoryTopicSerde
            )
        )
    }

    private fun createReservedStockStateStore(builder: StreamsBuilder) {
        val changelogConfig: HashMap<String, String> = HashMap()
        val reservedStockStoreBuilder = Stores
            .keyValueStoreBuilder(
                Stores.persistentKeyValueStore(LocalStores.RESERVED_STOCK.storeName),
                Serdes.String(),
                Serdes.Integer()
            )
            .withLoggingEnabled(changelogConfig)
        builder.addStateStore(reservedStockStoreBuilder)
    }
}