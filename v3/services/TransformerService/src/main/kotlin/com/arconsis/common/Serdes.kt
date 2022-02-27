package com.arconsis.common

import com.arconsis.domain.users.User
import com.arconsis.presentation.users.UserEventDto
import io.quarkus.kafka.client.serialization.ObjectMapperSerde

val userTopicSerde = ObjectMapperSerde(User::class.java)
val userRawTopicSerde = ObjectMapperSerde(UserEventDto::class.java)