package com.arconsis.domain.ordersvalidations

import com.arconsis.common.Message
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer

enum class OrderValidationType {
    VALID,
    INVALID
}

class OrderValidationMessage(override val type: OrderValidationType, override val payload: OrderValidation) :
    Message<OrderValidationType, OrderValidation>

class OrderValidationMessageDeserializer :
    ObjectMapperDeserializer<OrderValidationMessage>(OrderValidationMessage::class.java)