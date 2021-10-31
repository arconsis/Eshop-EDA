package com.arconsis.domain.orders

import com.arconsis.common.Topics
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class KafkaService {
    val kafkaProducer = createProducer()

    private fun createProducer(): KafkaProducer<String, Order> {
        val producerConfig = Properties()
        // TODO: Get these values from env vars
        producerConfig[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        producerConfig[ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG] = "true"
        producerConfig[ProducerConfig.RETRIES_CONFIG] = Int.MAX_VALUE.toString()
        producerConfig[ProducerConfig.ACKS_CONFIG] = "all"
        producerConfig[ProducerConfig.CLIENT_ID_CONFIG] = "order-service-producer"
        producerConfig[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] =
            "org.apache.kafka.common.serialization.StringSerializer"
        producerConfig[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = OrderSerializer::class.java
        return KafkaProducer(producerConfig)
    }

    fun sendOrderEvent(eventPair: Pair<String, Order>) {
        kafkaProducer.send(ProducerRecord(Topics.ORDERS.topicName, eventPair.first, eventPair.second)).get()
    }
}