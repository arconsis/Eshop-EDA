package com.arconsis.presentation.events.ordersvalidations

import com.arconsis.common.Topics
import com.arconsis.domain.orders.Order
import com.arconsis.domain.ordersValidations.OrderValidation
import com.arconsis.domain.ordersValidations.OrderValidationsService
import io.quarkus.kafka.client.serialization.ObjectMapperSerde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.KTable
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrderValidationEventsResource(private val orderValidationsService: OrderValidationsService) {
    fun consumeOrderValidationEvents(
        builder: StreamsBuilder,
        ordersTable: KTable<String, Order>,
        orderSerde: ObjectMapperSerde<Order>,
    ) {
        val orderValidationSerde = ObjectMapperSerde(OrderValidation::class.java)
        builder
            .stream(
                Topics.ORDERS_VALIDATIONS.topicName,
                Consumed.with(Serdes.String(), orderValidationSerde)
            )
            .handleOrderValidationEvents(ordersTable, orderSerde)
    }

    private fun KStream<String, OrderValidation>.handleOrderValidationEvents(
        ordersTable: KTable<String, Order>,
        orderSerde: ObjectMapperSerde<Order>
    ) = orderValidationsService.handleOrderValidationEvents(this, ordersTable, orderSerde)
}