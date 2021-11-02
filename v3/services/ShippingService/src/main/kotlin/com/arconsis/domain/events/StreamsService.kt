package com.arconsis.domain.events

import com.arconsis.common.Topics
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.isPaid
import com.arconsis.domain.shipments.Shipment
import com.arconsis.domain.shipments.ShipmentStatus
import com.arconsis.domain.shipments.toShipmentEvent
import io.quarkus.kafka.client.serialization.ObjectMapperSerde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.Produced
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Produces


@ApplicationScoped
class StreamsService {

  @Produces
  fun buildTopology(): Topology {
    val builder = StreamsBuilder()
    val orderTopicSerde = ObjectMapperSerde(Order::class.java)
    val shipmentTopicSerde = ObjectMapperSerde(Shipment::class.java)
    builder
      .stream(
        Topics.ORDERS.topicName,
        Consumed.with(Serdes.String(), orderTopicSerde)
      )
      .filter { _, order -> order.isPaid }
      .map { _, order ->
        val event = order.toShipmentEvent(ShipmentStatus.OUT_FOR_SHIPMENT)
        KeyValue.pair(event.key, event.value)
      }.to(Topics.SHIPMENTS.topicName, Produced.with(Serdes.String(), shipmentTopicSerde))

    return builder.build()
  }
}