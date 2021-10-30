package com.arconsis.domain.orders

import com.arconsis.common.Topics
import com.arconsis.presentation.orders.dto.OrderCreateDto
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.KafkaException
import java.util.*
import javax.enterprise.context.ApplicationScoped


@ApplicationScoped
class OrdersService {
  fun createOrder(orderCreateDto: OrderCreateDto): Order {
    val orderNo = UUID.randomUUID()
    val pendingOrder = orderCreateDto.toPendingOrder(orderNo)
    val event = createRequestOrderEvent(
      pendingOrder,
    )
    createProducer().use { kafkaProducer ->
      kafkaProducer.initTransactions()
      try {
        kafkaProducer.beginTransaction()
        kafkaProducer.send(ProducerRecord(Topics.Orders.name, event.first, event.second))
      } catch(e: KafkaException)  {
        kafkaProducer.abortTransaction()
      }
    }
    return pendingOrder
  }

  private fun createProducer(): KafkaProducer<String, Order> {
    val producerConfig = Properties()
    producerConfig[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
    producerConfig[ProducerConfig.TRANSACTIONAL_ID_CONFIG] = "OrderServiceInstance1"
    producerConfig[ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG] = "true"
    producerConfig[ProducerConfig.RETRIES_CONFIG] = Int.MAX_VALUE.toString()
    producerConfig[ProducerConfig.ACKS_CONFIG] = "all"
    producerConfig[ProducerConfig.CLIENT_ID_CONFIG] = "order-service-producer"
    producerConfig["key.serializer"] = "org.apache.kafka.common.serialization.StringSerializer"
    producerConfig["value.serializer"] = OrderDtoSerializer::class.java
    return KafkaProducer(producerConfig)
  }
}