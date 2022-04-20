package com.arconsis.domain.orders

import com.arconsis.domain.message.Message
import java.util.*

data class OrderMessage(override val payload: Order, override val messageId: UUID) : Message<Order>