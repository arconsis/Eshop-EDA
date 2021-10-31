package com.arconsis.domain.inventory

import com.arconsis.common.Topics
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrderStatus
import io.quarkus.kafka.client.serialization.ObjectMapperSerde
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.Produced
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class KafkaService {
  private var streams: KafkaStreams? = null

  fun consumeStream() {
    val builder = StreamsBuilder()
    val orderSerde = ObjectMapperSerde(Order::class.java)
    val orderValidationSerde = ObjectMapperSerde(OrderValidationDto::class.java)
    val orders: KStream<String, Order> = builder
      .stream(
        Topics.ORDERS.topicName,
        Consumed.with(Serdes.String(), orderSerde)
      )
    orders.filter { _, order ->
      order.status == OrderStatus.PENDING
    }.map { _, order ->
      val event = order.toOrderValidationEvent(OrderValidationType.ORDER_VALIDATED)
      KeyValue.pair(event.key, event.value)
    }.to(Topics.ORDERS_VALIDATIONS.topicName, Produced.with(Serdes.String(), orderValidationSerde))

    streams = KafkaStreams(
      builder.build(),
      baseStreamsConfig()
    )
    streams?.start()
  }

  fun stopConsumeStream() {
    streams?.close()
  }

  private fun baseStreamsConfig(): Properties {
    val config = Properties()
    config[StreamsConfig.APPLICATION_ID_CONFIG] = "warehouseservice"
    config[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
    config[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
    config[StreamsConfig.PROCESSING_GUARANTEE_CONFIG] = "exactly_once"
    config[StreamsConfig.COMMIT_INTERVAL_MS_CONFIG] = 1 //commit as fast as possible
    config[StreamsConfig.consumerPrefix(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG)] = 30000
    return config
  }
}