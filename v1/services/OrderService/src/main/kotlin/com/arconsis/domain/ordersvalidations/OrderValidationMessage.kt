package com.arconsis.domain.ordersvalidations

import com.arconsis.domain.message.Message
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer
import java.util.*

data class OrderValidationMessage(override val payload: OrderValidation, override val messageId: UUID) : Message<OrderValidation>

class OrderValidationMessageDeserializer : ObjectMapperDeserializer<OrderValidationMessage>(OrderValidationMessage::class.java)