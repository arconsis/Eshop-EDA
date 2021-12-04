package com.arconsis.domain.processedevents

import java.time.Instant
import java.util.*

data class ProcessedEvent(
    val eventId: UUID,
    val processedAt: Instant
)
