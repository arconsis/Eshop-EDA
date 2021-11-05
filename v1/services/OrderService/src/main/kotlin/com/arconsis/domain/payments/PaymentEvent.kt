package com.arconsis.domain.payments

import com.arconsis.common.Message
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer

enum class PaymentType {
    PAYMENT_PROCESSED,
    PAYMENT_FAILED
}

class PaymentMessage(override val type: PaymentType, override val payload: Payment) : Message<PaymentType, Payment>

class PaymentMessageDeserializer : ObjectMapperDeserializer<PaymentMessage>(PaymentMessage::class.java)