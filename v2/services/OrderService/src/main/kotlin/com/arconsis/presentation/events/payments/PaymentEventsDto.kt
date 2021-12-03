package com.arconsis.presentation.events.payments

import com.arconsis.domain.outboxevents.AggregateType
import com.arconsis.domain.outboxevents.OutboxEvent
import com.arconsis.domain.outboxevents.OutboxEventType
import com.arconsis.presentation.events.common.Schema
import com.arconsis.presentation.events.common.Source
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer
import java.time.Instant
import java.util.*

data class PaymentEventsDto(
    val schema: Schema? = null,
    val payload: PaymentEventPayload
)

data class PaymentEventPayload(
    val before: PaymentEventDtoPayload? = null,
    val after: PaymentEventDtoPayload,
    val source: Source,
    val op: String? = null,
    val tsMS: Long? = null,
    val transaction: Any? = null
)

data class PaymentEventDtoPayload(
    var id: String,
    var aggregate_type: String,
    var aggregate_id: String,
    val payload: String,
    val type: String,
    var created_at: Instant? = null,
    var updated_at: Instant? = null,
)

fun PaymentEventDtoPayload.toOutboxEvent() = OutboxEvent(
    id = UUID.fromString(id),
    aggregateId = UUID.fromString(aggregate_id),
    aggregateType = AggregateType.valueOf(aggregate_type),
    type = OutboxEventType.valueOf(type),
    payload = payload
)

class PaymentEventsDtoDeserializer : ObjectMapperDeserializer<PaymentEventsDto>(PaymentEventsDto::class.java)