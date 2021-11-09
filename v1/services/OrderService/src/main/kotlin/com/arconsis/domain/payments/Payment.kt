package com.arconsis.domain.payments

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer
import java.util.*

data class Payment(
    val transactionId: UUID,
    val orderId: UUID,
    val userId: UUID,
    val amount: Double,
    val currency: String,
    val status: PaymentStatus,
)

enum class PaymentStatus {
	SUCCESS,
    FAILED
}

class PaymentDeserializer : ObjectMapperDeserializer<Payment>(Payment::class.java)