package com.arconsis.domain.inventory

import com.arconsis.common.LocalStores
import com.arconsis.domain.orders.Order
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.Transformer
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.state.KeyValueStore
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class IncreaseStockValidator : Transformer<String, Pair<Order, Inventory>, KeyValue<String, Inventory>> {
    private lateinit var reservedStocksStore: KeyValueStore<String, Int>

    override fun init(context: ProcessorContext) {
        reservedStocksStore = context.getStateStore(LocalStores.RESERVED_STOCK.storeName)
    }

    override fun transform(key: String, orderAndStock: Pair<Order, Inventory>): KeyValue<String, Inventory> {
        //Process each order/inventory pair one at a time
        val (order, inventory) = orderAndStock

        //Look up locally 'reserved' stock from our state store
        val reserved = this.reservedStocksStore[order.productId]
        if (reserved != null) {
            var updatedReserved = reserved - order.quantity
            updatedReserved = if (updatedReserved < 0) 0 else updatedReserved
            reservedStocksStore.put(order.productId, updatedReserved)
        }

        return KeyValue.pair(
            order.orderId.toString(), Inventory(
                id = inventory.id,
                productId = inventory.productId,
                stock = this.reservedStocksStore[order.productId]
            )
        )
    }

    override fun close() {}
}