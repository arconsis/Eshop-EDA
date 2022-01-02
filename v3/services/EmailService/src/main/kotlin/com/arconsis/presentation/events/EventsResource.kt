package com.arconsis.presentation.events

import com.arconsis.common.Topics
import com.arconsis.domain.email.EmailService
import com.arconsis.domain.orders.Order
import io.quarkus.kafka.client.serialization.ObjectMapperSerde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.Consumed
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Produces

@ApplicationScoped
class EventsResource(private val emailService: EmailService) {

    @ApplicationScoped
    @Produces
    fun streamsBuilder(): StreamsBuilder {
        return StreamsBuilder()
    }

    @Produces
    fun createTopology(builder: StreamsBuilder): Topology {
        val orderTopicSerde = ObjectMapperSerde(Order::class.java)
        val ordersStream = builder
            .stream(
                Topics.ORDERS.topicName,
                Consumed.with(Serdes.String(), orderTopicSerde)
            )
        emailService.handleOrderEvents(ordersStream)
        return builder.build()
    }

    fun createUserTable() {}
}