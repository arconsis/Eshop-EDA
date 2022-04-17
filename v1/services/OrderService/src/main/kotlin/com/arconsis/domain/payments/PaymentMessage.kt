package com.arconsis.domain.payments

import com.arconsis.domain.message.Message
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer
import java.util.*

data class PaymentMessage(override val payload: Payment, override val messageId: UUID) : Message<Payment>

class PaymentMessageDeserializer : ObjectMapperDeserializer<PaymentMessage>(PaymentMessage::class.java)