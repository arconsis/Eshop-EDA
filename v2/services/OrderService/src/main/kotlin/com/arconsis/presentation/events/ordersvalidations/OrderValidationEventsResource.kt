package com.arconsis.presentation.events.ordersvalidations

import com.arconsis.domain.ordersvalidations.OrderValidation
import com.arconsis.domain.ordersvalidations.OrderValidationsService
import com.arconsis.domain.outboxevents.AggregateType
import com.arconsis.presentation.events.common.WarehouseEventDto
import com.arconsis.presentation.events.common.toOutboxEvent
import com.fasterxml.jackson.databind.ObjectMapper
import io.smallrye.mutiny.Uni
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Incoming
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrderValidationEventsResource(
    private val orderValidationsService: OrderValidationsService,
    private val objectMapper: ObjectMapper
) {

    @Incoming("warehouse-in")
    fun consumeWarehouseEvents(warehouseEventsDto: Record<String, WarehouseEventDto>): Uni<Void> {
        val orderValidationEventDto = warehouseEventsDto.value()
        val outboxEvent = orderValidationEventDto.payload.currentValue.toOutboxEvent()
        if (outboxEvent.aggregateType != AggregateType.ORDER_VALIDATION) {
            return Uni.createFrom().voidItem()
        }
        val orderValidation = objectMapper.readValue(
            outboxEvent.payload,
            OrderValidation::class.java
        )
        return orderValidationsService.handleOrderValidationEvents(orderValidation)
            .onFailure()
            .recoverWithNull()
    }
}