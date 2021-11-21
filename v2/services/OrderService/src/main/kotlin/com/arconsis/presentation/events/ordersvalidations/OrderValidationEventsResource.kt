package com.arconsis.presentation.events.ordersvalidations

import com.arconsis.domain.ordersvalidations.OrderValidation
import com.arconsis.domain.ordersvalidations.OrderValidationsService
import io.smallrye.mutiny.Uni
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Incoming
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrderValidationEventsResource(private val orderValidationsService: OrderValidationsService) {

    @Incoming("order-validation-in")
    suspend fun consumeOrderValidationEvents(orderValidationRecord: Record<String, OrderValidation>) {
        val orderValidation = orderValidationRecord.value()
        return orderValidationsService.handleOrderValidationEvents(orderValidation)
    }
}