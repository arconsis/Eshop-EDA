package com.arconsis.domain

import com.arconsis.common.Topics
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrderRequestEvent
import io.smallrye.mutiny.coroutines.awaitSuspending
import io.smallrye.reactive.messaging.kafka.KafkaClientService
import io.smallrye.reactive.messaging.kafka.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class EventsService(kafkaClientService: KafkaClientService) {
  val kafkaProducer: KafkaProducer<String, Order> = kafkaClientService.getProducer("orders-out")

  suspend fun sendOrderEvent(event: OrderRequestEvent) {
    kafkaProducer.send(ProducerRecord(Topics.ORDERS.topicName, event.key, event.value)).awaitSuspending()
  }
}