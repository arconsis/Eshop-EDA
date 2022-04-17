package com.arconsis.domain.orders

import com.arconsis.domain.message.Message
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer
import java.util.*

data class OrderMessage(override val payload: Order, override val messageId: UUID) : Message<Order>

class OrderMessageDeserializer : ObjectMapperDeserializer<OrderMessage>(OrderMessage::class.java)