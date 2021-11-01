package com.arconsis.domain.events

import com.arconsis.common.Topics
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrderStatus
import com.arconsis.domain.ordersValidations.OrderValidation
import com.arconsis.domain.ordersValidations.isValid
import io.quarkus.kafka.client.serialization.ObjectMapperSerde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.Produced
import java.util.*
import javax.enterprise.inject.Produces

class SteamsService {
  @Produces
  fun createOrdersValidationsTopology(): Topology {
    val builder = StreamsBuilder()
    val orderValidationSerde = ObjectMapperSerde(OrderValidation::class.java)
    val orderSerde = ObjectMapperSerde(Order::class.java)
    val ordersTable = builder.table(Topics.ORDERS.topicName, Consumed.with(Serdes.String(), orderSerde))

    builder
      .stream(
        Topics.ORDERS_VALIDATIONS.topicName,
        Consumed.with(Serdes.String(), orderValidationSerde)
      )
      .filter { _, orderValidation ->
        orderValidation.isValid
      }
      .join(ordersTable) { _, order ->
        order
      }
      .map { _, orderValidation ->
        val validOrder = orderValidation.copy(status = OrderStatus.VALID)
        KeyValue.pair(UUID.randomUUID().toString(), validOrder)
      }
      .to(Topics.ORDERS.topicName, Produced.with(Serdes.String(), orderSerde))

    return builder.build()
  }
}