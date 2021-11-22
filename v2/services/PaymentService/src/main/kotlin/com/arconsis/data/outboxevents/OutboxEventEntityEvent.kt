package com.arconsis.data.outboxevents

import com.arconsis.domain.outboxevents.AggregateType
import com.arconsis.domain.outboxevents.OutboxEvent
import com.arconsis.domain.payments.PaymentStatus
import com.fasterxml.jackson.databind.JsonNode
import io.debezium.outbox.quarkus.ExportedEvent
import java.time.Instant
import java.util.*

class OutboxEventEntityEvent(
    private val aggregateId: UUID,
    private val aggregateType: AggregateType,
    private val type: PaymentStatus,
    private val node: JsonNode,
    private val kTimestamp: Instant = Instant.now()
) : ExportedEvent<String, JsonNode> {

    override fun getAggregateId(): String {
        return aggregateId.toString()
    }

    override fun getPayload(): JsonNode {
        return node
    }

    override fun getType(): String {
        return type.name
    }

    override fun getTimestamp(): Instant {
        return kTimestamp
    }

    override fun getAggregateType(): String {
        return aggregateType.name
    }
}

fun OutboxEventEntityEvent.toOutboxEvent() = OutboxEvent(
    id = UUID.fromString(aggregateId),
    aggregateId = UUID.fromString(aggregateId),
    aggregateType = AggregateType.valueOf(aggregateType),
    payload = payload
)