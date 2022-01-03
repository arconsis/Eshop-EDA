package com.arconsis.common

import com.arconsis.domain.orders.Order
import com.arconsis.domain.users.User
import io.quarkus.kafka.client.serialization.ObjectMapperSerde

val orderTopicSerde = ObjectMapperSerde(Order::class.java)
val userTopicSerde = ObjectMapperSerde(User::class.java)