package com.arconsis.common

import com.arconsis.domain.orders.Order
import com.arconsis.domain.payments.Payment
import io.quarkus.kafka.client.serialization.ObjectMapperSerde

val orderSerde = ObjectMapperSerde(Order::class.java)
val paymentTopicSerde = ObjectMapperSerde(Payment::class.java)