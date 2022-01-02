package com.arconsis.presentation.events

import com.arconsis.common.Topics
import com.arconsis.common.userTopicSerde
import com.arconsis.domain.users.User
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KTable
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Produces

@ApplicationScoped
class UsersKTableFactory {

    @Produces
    fun createUsersKTable(builder: StreamsBuilder): KTable<String, User> {
        return builder.table(Topics.USERS.topicName, Consumed.with(Serdes.String(), userTopicSerde))
    }
}