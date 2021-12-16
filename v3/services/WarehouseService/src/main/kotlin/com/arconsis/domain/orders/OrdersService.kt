package com.arconsis.domain.orders

import com.arconsis.common.Topics
import com.arconsis.domain.inventory.Inventory
import com.arconsis.domain.ordervalidations.OrderValidation
import com.arconsis.domain.ordervalidations.OrderValidationType
import com.arconsis.domain.shipments.Shipment
import com.arconsis.domain.shipments.ShipmentStatus
import com.arconsis.domain.shipments.toShipmentEvent
import io.quarkus.kafka.client.serialization.ObjectMapperSerde
import io.smallrye.mutiny.Uni
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.kstream.*
import java.time.Duration
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrdersService {
    fun handleOrderEvents(
        stream: KStream<String, Order>,
        orderValidationSerde: ObjectMapperSerde<OrderValidation>,
        shipmentTopicSerde: ObjectMapperSerde<Shipment>,
        inventoryTable: KTable<String, Inventory>
    ): BranchedKStream<String, Order> = stream.split()
        .branch(
            { _, order -> order.isRequested },
            Branched.withConsumer {
                it.handlePendingOrder(orderValidationSerde, inventoryTable)
            }
        )
        .branch(
            { _, order -> order.isPaid },
            Branched.withConsumer {
                it.handlePaidOrder(shipmentTopicSerde, inventoryTable)
            }
        )


    private fun KStream<String, Order>.handlePendingOrder(
        orderValidationSerde: ObjectMapperSerde<OrderValidation>,
        inventoryTable: KTable<String, Inventory>
    ) = mapValues { order ->
        // TODO: Add logic to check validity of the order
        val event = order.toOrderValidationEvent(OrderValidationType.VALIDATED)
        event.value
    }.join().to(Topics.ORDERS_VALIDATIONS.topicName, Produced.with(Serdes.String(), orderValidationSerde))

    private fun KStream<String, Order>.handlePaidOrder(
        shipmentTopicSerde: ObjectMapperSerde<Shipment>,
        inventoryTable: KTable<String, Inventory>
    ) = mapValues { order ->
        // added some latency to simulate remote call with some courier
        Uni.createFrom().voidItem().onItem().delayIt().by(Duration.ofMillis(5000)).await().indefinitely()
        val event = order.toShipmentEvent(ShipmentStatus.SHIPPED)
        event.value
    }.to(Topics.SHIPMENTS.topicName, Produced.with(Serdes.String(), shipmentTopicSerde))
}