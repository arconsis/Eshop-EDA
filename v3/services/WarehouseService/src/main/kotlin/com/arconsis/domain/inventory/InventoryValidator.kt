package com.arconsis.domain.inventory

import com.arconsis.common.LocalStores
import com.arconsis.domain.orders.Order
import com.arconsis.domain.ordervalidations.OrderValidation
import com.arconsis.domain.ordervalidations.OrderValidationType
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.Transformer
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.state.KeyValueStore
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class InventoryValidator : Transformer<String, Pair<Order, Inventory>, KeyValue<String, OrderValidation>> {
    private var reservedStocksStore: KeyValueStore<String, Int>? = null

    override fun init(context: ProcessorContext) {
        reservedStocksStore = context.getStateStore(LocalStores.RESERVED_STOCK.storeName)
    }

    override fun transform(key: String, orderAndStock: Pair<Order, Inventory>): KeyValue<String, OrderValidation> {
        //Process each order/inventory pair one at a time
        val order = orderAndStock.first
        val warehouseStockCount = orderAndStock.second.stock

        //Look up locally 'reserved' stock from our state store
        var reserved = reservedStocksStore!![order.productId]
        if (reserved == null) {
            reserved = 0
        }

        //If there is enough stock available (considering both warehouse inventory and reserved stock) validate the order
        val orderValidation = if (warehouseStockCount - reserved - order.quantity >= 0) {
            //reserve the stock by adding it to the 'reserved' store
            reservedStocksStore!!.put(order.productId, reserved + order.quantity)
            //validate the order
            OrderValidation(
                type = OrderValidationType.VALIDATED,
                userId = order.userId,
                orderId = order.orderId,
                productId = order.productId,
                quantity = order.quantity
            )
        } else {
            //fail the order
            OrderValidation(
                type = OrderValidationType.INVALID,
                userId = order.userId,
                orderId = order.orderId,
                productId = order.productId,
                quantity = order.quantity
            )
        }
        return KeyValue.pair(orderValidation.userId.toString(), orderValidation)
    }

    override fun close() {}
}