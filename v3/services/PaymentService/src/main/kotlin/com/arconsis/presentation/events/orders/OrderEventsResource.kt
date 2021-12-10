package com.arconsis.presentation.events.orders

import com.arconsis.common.Topics
import com.arconsis.domain.orders.Order
import com.arconsis.domain.payments.Payment
import com.arconsis.domain.payments.PaymentsService
import io.quarkus.kafka.client.serialization.ObjectMapperSerde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KStream
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrderEventsResource(
    private val paymentsService: PaymentsService,
) {
    fun buildOrderEventsTopology(builder: StreamsBuilder) {
        val orderSerde = ObjectMapperSerde(Order::class.java)
        val paymentTopicSerde = ObjectMapperSerde(Payment::class.java)
        builder
            .stream(
                Topics.ORDERS.topicName,
                Consumed.with(Serdes.String(), orderSerde)
            )
            .handleOrderEvents(paymentTopicSerde)
    }

    private fun KStream<String, Order>.handleOrderEvents(paymentTopicSerde: ObjectMapperSerde<Payment>): Unit =
        paymentsService.handleOrderEvents(this, paymentTopicSerde)
}