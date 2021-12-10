package com.arconsis.presentation.events

import com.arconsis.common.Topics
import com.arconsis.domain.orders.Order
import com.arconsis.presentation.events.ordersvalidations.OrderValidationEventsResource
import com.arconsis.presentation.events.payments.PaymentEventsResource
import com.arconsis.presentation.events.shipments.ShipmentEventsResource
import io.quarkus.kafka.client.serialization.ObjectMapperSerde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.Consumed
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Produces

@ApplicationScoped
class EventsResource(
    private val orderValidationEventsResource: OrderValidationEventsResource,
    private val paymentEventsResource: PaymentEventsResource,
    private val shipmentEventsResource: ShipmentEventsResource,
) {
    @Produces
    fun createTopology(): Topology {
        val builder = StreamsBuilder()
        val orderSerde = ObjectMapperSerde(Order::class.java)
        val ordersTable = builder.table(Topics.ORDERS.topicName, Consumed.with(Serdes.String(), orderSerde))
        orderValidationEventsResource.consumeOrderValidationEvents(builder, ordersTable, orderSerde)
        paymentEventsResource.consumePaymentEvents(builder, ordersTable, orderSerde)
        shipmentEventsResource.consumeShipmentEvents(builder, ordersTable, orderSerde)
        return builder.build()
    }
}