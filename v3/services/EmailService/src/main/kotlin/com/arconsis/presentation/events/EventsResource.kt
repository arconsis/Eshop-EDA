package com.arconsis.presentation.events

import com.arconsis.common.Topics
import com.arconsis.common.orderTopicSerde
import com.arconsis.common.userTopicSerde
import com.arconsis.domain.email.EmailService
import com.arconsis.domain.users.User
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KTable
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Produces

@ApplicationScoped
class EventsResource(private val emailService: EmailService) {

    @Produces
    fun createTopology(): Topology {
        val builder = StreamsBuilder()
        val usersTable = createUsersKTable(builder)
        val ordersStream = builder
            .stream(
                Topics.ORDERS.topicName,
                Consumed.with(Serdes.String(), orderTopicSerde)
            )
        emailService.handleOrderEvents(ordersStream, usersTable)
        return builder.build()
    }

    fun createUsersKTable(builder: StreamsBuilder): KTable<String, User> {
        return builder.table(Topics.USERS.topicName, Consumed.with(Serdes.String(), userTopicSerde))
    }
}