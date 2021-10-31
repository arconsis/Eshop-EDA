package com.arconsis.domain.inventory

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
    val inventorySerde = ObjectMapperSerde(Inventory::class.java)
    val orders: KStream<String, Order> = builder
      .stream(
        "Orders",
        Consumed.with(Serdes.String(), orderSerde)
      )
    orders.filter { _, order ->
      order.status == OrderStatus.PENDING
    }.map { _, order ->
      val event = order.toWarehouseEvent(WarehouseEventType.ORDER_VALIDATED)
      KeyValue.pair(event.key, event.value)
    }.to("Warehouse", Produced.with(Serdes.String(), inventorySerde))

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
    // Workaround for a known issue with RocksDB in environments where you have only 1 cpu core.
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "warehouseservice")
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    config.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, "exactly_once")
    config.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1) //commit as fast as possible
    config.put(StreamsConfig.consumerPrefix(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG), 30000)
    return config
  }
}