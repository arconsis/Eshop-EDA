package com.arconsis.domain.payments

import com.arconsis.domain.message.Message
import java.util.*

data class PaymentMessage(override val payload: Payment, override val messageId: UUID) : Message<Payment>