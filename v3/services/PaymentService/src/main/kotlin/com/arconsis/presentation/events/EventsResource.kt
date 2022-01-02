package com.arconsis.presentation.events

import com.arconsis.common.Topics
import com.arconsis.common.orderSerde
import com.arconsis.domain.payments.PaymentsService
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.Consumed
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Produces

@ApplicationScoped
class EventsResource(
    val paymentsService: PaymentsService
) {
    @Produces
    fun createTopology(): Topology {
        val builder = StreamsBuilder()

        val ordersStream = builder
            .stream(
                Topics.ORDERS.topicName,
                Consumed.with(Serdes.String(), orderSerde)
            )

        paymentsService.handleOrderEvents(ordersStream)
        return builder.build()
    }
}