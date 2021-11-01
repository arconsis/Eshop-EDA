package com.arconsis.domain

import com.arconsis.common.Topics
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrderRequestEvent
import com.arconsis.domain.ordersValidations.OrderValidation
import com.arconsis.domain.ordersValidations.OrderValidationType
import io.quarkus.kafka.client.serialization.ObjectMapperSerde
import io.smallrye.mutiny.coroutines.awaitSuspending
import io.smallrye.reactive.messaging.kafka.KafkaClientService
import io.smallrye.reactive.messaging.kafka.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.Produced
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Produces

@ApplicationScoped
class EventsService(kafkaClientService: KafkaClientService) {
  val kafkaProducer: KafkaProducer<String, Order> = kafkaClientService.getProducer("orders-out")

  suspend fun sendOrderEvent(event: OrderRequestEvent) {
    kafkaProducer.send(ProducerRecord(Topics.ORDERS.topicName, event.key, event.value)).awaitSuspending()
  }

  @Produces
  fun createOrdersValidationsTopology(): Topology {
    val builder = StreamsBuilder()
    val orderValidationSerde = ObjectMapperSerde(OrderValidation::class.java)
    builder
      .stream(
        Topics.ORDERS_VALIDATIONS.topicName,
        Consumed.with(Serdes.String(), orderValidationSerde)
      )
      .filter { _, orderValidation ->
        orderValidation.type == OrderValidationType.VALID
      }
      .join {

      }
      .map { _, orderValidation ->
        val validOrder = orderValidation.copy()
        KeyValue.pair(UUID.randomUUID().toString(), validOrder)
      }
      .to(Topics.PAYMENTS.topicName, Produced.with(Serdes.String(), orderValidationSerde))
  }
}