package com.arconsis.domain.orders

import com.arconsis.common.*
import com.arconsis.domain.inventory.IncreaseStockValidator
import com.arconsis.domain.inventory.Inventory
import com.arconsis.domain.inventory.ReserveStockValidator
import com.arconsis.domain.ordervalidations.toOrderValidationEvent
import com.arconsis.domain.shipments.ShipmentStatus
import com.arconsis.domain.shipments.toShipmentEvent
import io.smallrye.mutiny.Uni
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.kstream.*
import java.time.Duration
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrdersService(val inventoryTable: KTable<String, Inventory>) {

    fun handleOrderEvents(stream: KStream<String, Order>): BranchedKStream<String, Order> {
        return stream.split()
            .branch(
                { _, order -> order.isRequested },
                Branched.withConsumer {
                    it.handlePendingOrder()
                }
            )
            .branch(
                { _, order -> order.isPaid },
                Branched.withConsumer {
                    it.handlePaidOrder()
                }
            )
            .branch(
                { _, order -> order.isPaymentFailed },
                Branched.withConsumer {
                    it.handleFailedPaymentOrder()
                }
            )
    }


    private fun KStream<String, Order>.handlePendingOrder() {
        return selectKey { _, order ->
            order.productId
        }
            // Join Orders to Inventory so we can compare each order to its corresponding stock value
            .join(
                inventoryTable,
                { order, inventory -> Pair(order, inventory) },
                Joined.with(Serdes.String(), orderTopicSerde, inventoryTopicSerde)
            )
            // Validate the order based on how much stock we have both in the warehouse and locally 'reserved' stock
            .transform(
                { ReserveStockValidator() },
                LocalStores.RESERVED_STOCK.storeName
            )
            //Push the result into the Order Validations topic
            .mapValues { _, orderValidation ->
                val event = orderValidation.toOrderValidationEvent()
                event.value
            }
            .to(Topics.ORDERS_VALIDATIONS.topicName, Produced.with(Serdes.String(), orderValidationSerde))
    }

    private fun KStream<String, Order>.handlePaidOrder() {
        return mapValues { order ->
            // added some latency to simulate remote call with some courier
            Uni.createFrom().voidItem().onItem().delayIt().by(Duration.ofSeconds(5)).await().indefinitely()
            val event = order.toShipmentEvent(ShipmentStatus.SHIPPED)
            event.value
        }.to(Topics.SHIPMENTS.topicName, Produced.with(Serdes.String(), shipmentTopicSerde))
    }

    private fun KStream<String, Order>.handleFailedPaymentOrder(): KStream<String, Inventory> {
        return selectKey { _, order ->
            order.productId
        }
            // Join Orders to Inventory so we can compare each order to its corresponding stock value
            .join(
                inventoryTable,
                { order, inventory -> Pair(order, inventory) },
                Joined.with(Serdes.String(), orderTopicSerde, inventoryTopicSerde)
            )
            // Validate the order based on how much stock we have both in the warehouse and locally 'reserved' stock
            .transform(
                { IncreaseStockValidator() },
                LocalStores.RESERVED_STOCK.storeName
            )
    }
}