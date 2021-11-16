package com.arconsis.domain.events

import com.arconsis.common.Topics
import com.arconsis.domain.orders.*
import com.arconsis.domain.shipments.Shipment
import com.arconsis.domain.shipments.ShipmentStatus
import com.arconsis.domain.shipments.toShipmentEvent
import io.quarkus.kafka.client.serialization.ObjectMapperSerde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.Produced
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Produces

@ApplicationScoped
class StreamsService {

    @Produces
    fun createTopology(): Topology {
        val builder = StreamsBuilder()
        val orderTopicSerde = ObjectMapperSerde(Order::class.java)

        createOrdersValidationsStream(builder, orderTopicSerde)
        createShipmentsStream(builder, orderTopicSerde)
        return builder.build()
    }

    private fun createOrdersValidationsStream(
        builder: StreamsBuilder,
        orderTopicSerde: ObjectMapperSerde<Order>,
    ) {
        val orderValidationSerde = ObjectMapperSerde(OrderValidation::class.java)
        builder
            .stream(
                Topics.ORDERS.topicName,
                Consumed.with(Serdes.String(), orderTopicSerde)
            )
            .filter { _, order -> order.isPending }
            .mapValues { order ->
                // TODO: Add logic to check validity of the order
                val event = order.toOrderValidationEvent(OrderValidationType.VALID)
                event.value
            }.to(Topics.ORDERS_VALIDATIONS.topicName, Produced.with(Serdes.String(), orderValidationSerde))
    }

    private fun createShipmentsStream(
        builder: StreamsBuilder,
        orderTopicSerde: ObjectMapperSerde<Order>,
    ) {
        val shipmentTopicSerde = ObjectMapperSerde(Shipment::class.java)
        builder
            .stream(
                Topics.ORDERS.topicName,
                Consumed.with(Serdes.String(), orderTopicSerde)
            )
            .filter { _, order -> order.isPaid }
            .mapValues { order ->
                // TODO: add some latency to simulate remote call with some courier
                val event = order.toShipmentEvent(ShipmentStatus.OUT_FOR_SHIPMENT)
                event.value
            }.to(Topics.SHIPMENTS.topicName, Produced.with(Serdes.String(), shipmentTopicSerde))
    }
}