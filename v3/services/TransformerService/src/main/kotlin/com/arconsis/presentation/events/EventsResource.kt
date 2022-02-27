package com.arconsis.presentation.events

import com.arconsis.common.Topics
import com.arconsis.common.userRawTopicSerde
import com.arconsis.common.userTopicSerde
import com.arconsis.domain.users.User
import com.arconsis.presentation.users.UserEventKeyDto
import com.arconsis.presentation.users.toOutboxEvent
import com.fasterxml.jackson.databind.ObjectMapper
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
class EventsResource(private val objectMapper: ObjectMapper) {

    @Produces
    fun createTopology(): Topology {
        val builder = StreamsBuilder()
        consumesUserDatabaseChanges(builder)
        return builder.build()
    }

    fun consumesUserDatabaseChanges(builder: StreamsBuilder) = builder
            .stream(
                Topics.USERS_RAW.topicName,
                Consumed.with(ObjectMapperSerde(UserEventKeyDto::class.java), userRawTopicSerde)
            )
            .map { _, userRaw ->
                val user = objectMapper.readValue(userRaw.payload.currentValue.toOutboxEvent().payload, User::class.java)
                KeyValue.pair(user.id.toString(), user)
            }
            .to(Topics.USERS.topicName, Produced.with(Serdes.String(), userTopicSerde))
}