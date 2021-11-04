package com.arconsis.domain.payments

import com.arconsis.common.Message

enum class PaymentType {
    PAYMENT_PROCESSED,
    PAYMENT_FAILED
}

data class PaymentEvent(
    val key: String,
    val value: Message<PaymentType, Payment>,
)