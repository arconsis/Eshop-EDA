package com.arconsis.presentation.events

import com.arconsis.common.Topics
import com.arconsis.common.orderValidationSerde
import com.arconsis.common.paymentTopicSerde
import com.arconsis.common.shipmentTopicSerde
import com.arconsis.domain.ordersValidations.OrderValidationsService
import com.arconsis.domain.payments.PaymentsService
import com.arconsis.domain.shipments.ShipmentsService
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.Consumed
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Produces

@ApplicationScoped
class EventsResource(
    private val orderValidationsService: OrderValidationsService,
    private val paymentsService: PaymentsService,
    private val shipmentsService: ShipmentsService,
) {

    @ApplicationScoped
    @Produces
    fun streamsBuilder(): StreamsBuilder {
        return StreamsBuilder()
    }

    @Produces
    fun createTopology(builder: StreamsBuilder): Topology {

        val orderValidationsStream = builder
            .stream(
                Topics.ORDERS_VALIDATIONS.topicName,
                Consumed.with(Serdes.String(), orderValidationSerde)
            )
        orderValidationsService.handleOrderValidationEvents(orderValidationsStream)

        val paymentsStream = builder
            .stream(
                Topics.PAYMENTS.topicName,
                Consumed.with(Serdes.String(), paymentTopicSerde)
            )

        paymentsService.handlePaymentEvents(paymentsStream)

        val shipmentsStream = builder
            .stream(
                Topics.SHIPMENTS.topicName,
                Consumed.with(Serdes.String(), shipmentTopicSerde)
            )

        shipmentsService.handleShipmentEvents(shipmentsStream)
        return builder.build()
    }
}