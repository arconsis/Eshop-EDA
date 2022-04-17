package com.arconsis.domain.ordersvalidations

import com.arconsis.domain.message.Message
import java.util.*

data class OrderValidationMessage(override val payload: OrderValidation, override val messageId: UUID) : Message<OrderValidation>