package com.arconsis.domain.orders

import com.arconsis.domain.outboxevents.AggregateType
import com.arconsis.domain.outboxevents.CreateOutboxEvent
import com.arconsis.domain.outboxevents.OutboxEventType
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.*

data class CreateOrder(
    val userId: UUID,
    val amount: Double,
    val currency: String,
    val productId: String,
    val quantity: Int,
)

data class Order(
    val id: UUID,
    val userId: UUID,
    val amount: Double,
    val currency: String,
    val productId: String,
    val quantity: Int,
    val status: OrderStatus,
)

enum class OrderStatus {
    REQUESTED,
    VALIDATED,
    OUT_OF_STOCK,
    PAID,
    SHIPPED,
    COMPLETED,
    PAYMENT_FAILED,
    CANCELLED,
    REFUNDED
}

fun Order.toCreateOutboxEvent(objectMapper: ObjectMapper): CreateOutboxEvent = CreateOutboxEvent(
    aggregateType = AggregateType.ORDER,
    aggregateId = this.id,
    type = this.status.toOutboxEventType(),
    payload = objectMapper.writeValueAsString(this)
)

private fun OrderStatus.toOutboxEventType(): OutboxEventType = when(this) {
    OrderStatus.REQUESTED -> OutboxEventType.ORDER_REQUESTED
    OrderStatus.VALIDATED -> OutboxEventType.ORDER_VALIDATED
    OrderStatus.OUT_OF_STOCK -> OutboxEventType.ORDER_OUT_OF_STOCK
    OrderStatus.PAID -> OutboxEventType.ORDER_PAID
    OrderStatus.SHIPPED -> OutboxEventType.ORDER_SHIPPED
    OrderStatus.COMPLETED -> OutboxEventType.ORDER_COMPLETED
    OrderStatus.PAYMENT_FAILED -> OutboxEventType.ORDER_PAYMENT_FAILED
    OrderStatus.CANCELLED -> OutboxEventType.ORDER_CANCELLED
    OrderStatus.REFUNDED -> OutboxEventType.ORDER_REFUNDED
}