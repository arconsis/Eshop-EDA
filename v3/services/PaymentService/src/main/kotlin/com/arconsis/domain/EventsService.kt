package com.arconsis.domain

import com.arconsis.common.Topics
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.isValidated
import com.arconsis.domain.payments.Payment
import com.arconsis.domain.payments.PaymentStatus
import com.arconsis.domain.payments.toPaymentEvent
import io.quarkus.kafka.client.serialization.ObjectMapperSerde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.Produced
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Produces

@ApplicationScoped
class EventsService {

  @Produces
  fun buildTopology(): Topology {
    val builder = StreamsBuilder()
    val orderSerde = ObjectMapperSerde(Order::class.java)
    val paymentTopicSerde = ObjectMapperSerde(Payment::class.java)
    builder
      .stream(
        Topics.ORDERS.topicName,
        Consumed.with(Serdes.String(), orderSerde)
      )
      .filter { _, order -> order.isValidated }
      .map { _, order ->
        // TODO: Add logic to make remote payments
        val event = order.toPaymentEvent(PaymentStatus.SUCCESS)
        KeyValue.pair(event.key, event.value)
      }.to(Topics.PAYMENTS.topicName, Produced.with(Serdes.String(), paymentTopicSerde))

    return builder.build()
  }
}