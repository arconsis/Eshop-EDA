package com.arconsis.presentation.events.shipments

import com.arconsis.common.Topics
import com.arconsis.domain.orders.Order
import com.arconsis.domain.shipments.Shipment
import com.arconsis.domain.shipments.ShipmentsService
import io.quarkus.kafka.client.serialization.ObjectMapperSerde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.KTable
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ShipmentEventsResource(private val shipmentsService: ShipmentsService) {
    fun consumeShipmentEvents(
        builder: StreamsBuilder,
        ordersTable: KTable<String, Order>,
        orderSerde: ObjectMapperSerde<Order>,
    ) {
        val shipmentTopicSerde = ObjectMapperSerde(Shipment::class.java)
        builder
            .stream(
                Topics.SHIPMENTS.topicName,
                Consumed.with(Serdes.String(), shipmentTopicSerde)
            )
            .handleShipmentEvents(ordersTable, orderSerde)
    }

    private fun KStream<String, Shipment>.handleShipmentEvents(
        ordersTable: KTable<String, Order>,
        orderSerde: ObjectMapperSerde<Order>
    ) = shipmentsService.handleShipmentEvents(this, ordersTable, orderSerde)
}