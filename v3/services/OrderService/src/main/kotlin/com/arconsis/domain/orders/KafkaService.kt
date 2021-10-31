package com.arconsis.domain.orders

import com.arconsis.common.Topics
import io.smallrye.mutiny.coroutines.awaitSuspending
import io.smallrye.reactive.messaging.kafka.KafkaClientService
import io.smallrye.reactive.messaging.kafka.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class KafkaService(kafkaClientService: KafkaClientService) {

    val kafkaProducer: KafkaProducer<String, Order> = kafkaClientService.getProducer("orders-out")


    suspend fun sendOrderEvent(eventPair: Pair<String, Order>) {
        kafkaProducer.send(ProducerRecord(Topics.ORDERS.topicName, eventPair.first, eventPair.second)).awaitSuspending()
    }
}