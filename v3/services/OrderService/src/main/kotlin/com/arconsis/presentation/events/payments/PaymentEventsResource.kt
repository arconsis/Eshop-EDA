package com.arconsis.presentation.events.payments

import com.arconsis.common.Topics
import com.arconsis.domain.orders.Order
import com.arconsis.domain.payments.Payment
import com.arconsis.domain.payments.PaymentsService
import io.quarkus.kafka.client.serialization.ObjectMapperSerde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.KTable
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PaymentEventsResource(private val paymentsService: PaymentsService) {
    fun consumePaymentEvents(
        builder: StreamsBuilder,
        ordersTable: KTable<String, Order>,
        orderSerde: ObjectMapperSerde<Order>,
    ) {
        val paymentTopicSerde = ObjectMapperSerde(Payment::class.java)
        builder
            .stream(
                Topics.PAYMENTS.topicName,
                Consumed.with(Serdes.String(), paymentTopicSerde)
            )
            .handlePaymentEvents(ordersTable, orderSerde)
    }

    private fun KStream<String, Payment>.handlePaymentEvents(
        ordersTable: KTable<String, Order>,
        orderSerde: ObjectMapperSerde<Order>
    ) = paymentsService.handlePaymentEvents(this, ordersTable, orderSerde)
}