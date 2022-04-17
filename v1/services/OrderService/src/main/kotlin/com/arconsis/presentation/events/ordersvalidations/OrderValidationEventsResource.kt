package com.arconsis.presentation.events.ordersvalidations

import com.arconsis.domain.ordersvalidations.OrderValidationMessage
import com.arconsis.domain.ordersvalidations.OrderValidationsService
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Incoming
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrderValidationEventsResource(private val orderValidationsService: OrderValidationsService) {

    @Incoming("order-validation-in")
    suspend fun consumeOrderValidationEvents(orderValidationMessageRecord: Record<String, OrderValidationMessage>) {
        // TODO: Log the possible error here
        val orderValidationMessage = orderValidationMessageRecord.value()
        runCatching {
            orderValidationsService.handleOrderValidationEvents(orderValidationMessage)
        }.getOrNull()
    }
}