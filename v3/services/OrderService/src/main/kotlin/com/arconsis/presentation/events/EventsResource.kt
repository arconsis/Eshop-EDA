package com.arconsis.presentation.events

import com.arconsis.common.*
import com.arconsis.domain.orders.Order
import com.arconsis.domain.ordersValidations.OrderValidationsService
import com.arconsis.domain.payments.PaymentsService
import com.arconsis.domain.shipments.ShipmentsService
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KTable
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Produces

@ApplicationScoped
class EventsResource(
    private val orderValidationsService: OrderValidationsService,
    private val paymentsService: PaymentsService,
    private val shipmentsService: ShipmentsService,
) {

    @Produces
    fun createTopology(): Topology {

        val builder = StreamsBuilder()

        val ordersTable = createOrdersKTable(builder)

        val orderValidationsStream = builder
            .stream(
                Topics.ORDERS_VALIDATIONS.topicName,
                Consumed.with(Serdes.String(), orderValidationSerde)
            )
        orderValidationsService.handleOrderValidationEvents(orderValidationsStream, ordersTable)

        val paymentsStream = builder
            .stream(
                Topics.PAYMENTS.topicName,
                Consumed.with(Serdes.String(), paymentTopicSerde)
            )

        paymentsService.handlePaymentEvents(paymentsStream, ordersTable)

        val shipmentsStream = builder
            .stream(
                Topics.SHIPMENTS.topicName,
                Consumed.with(Serdes.String(), shipmentTopicSerde)
            )

        shipmentsService.handleShipmentEvents(shipmentsStream, ordersTable)
        return builder.build()
    }

    private fun createOrdersKTable(builder: StreamsBuilder): KTable<String, Order> {
        return builder.table(Topics.ORDERS.topicName, Consumed.with(Serdes.String(), orderSerde))
    }
}