package com.arconsis.domain.message

import java.util.*

interface Message <T> {
    val payload: T
    val messageId: UUID
}