package com.arconsis.domain

import com.arconsis.common.Topics
import com.arconsis.domain.orders.*
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
class EventService {

    @Produces
    fun buildTopology(): Topology {
        val builder = StreamsBuilder()
        val orderSerde = ObjectMapperSerde(Order::class.java)
        val orderValidationSerde = ObjectMapperSerde(OrderValidationDto::class.java)
        builder
            .stream(
                Topics.ORDERS.topicName,
                Consumed.with(Serdes.String(), orderSerde)
            )
            .filter { _, order -> order.isPending }
            .map { _, order ->
                // TODO: Add logic to check validity of the order
                val event = order.toOrderValidationEvent(OrderValidationType.ORDER_VALIDATED)
                KeyValue.pair(event.key, event.value)
            }.to(Topics.ORDERS_VALIDATIONS.topicName, Produced.with(Serdes.String(), orderValidationSerde))

        return builder.build()
    }
}
