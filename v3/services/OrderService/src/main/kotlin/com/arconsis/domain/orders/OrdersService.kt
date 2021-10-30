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
        val event = creatOrderRequestEventPair(
            pendingOrder,
        )

        sendOrderEvent(event)
        return pendingOrder
    }

    private fun sendOrderEvent(eventPair: Pair<String, Order>) {
        val kafkaProducer = createProducer()
        kafkaProducer.initTransactions()
        try {
            kafkaProducer.beginTransaction()
            // Blocking until https://issues.apache.org/jira/browse/KAFKA-12227
            kafkaProducer.send(ProducerRecord(Topics.ORDERS.topicName, eventPair.first, eventPair.second)).get()
            kafkaProducer.commitTransaction()
            kafkaProducer.close()
        } catch (e: KafkaException) {
            kafkaProducer.abortTransaction()
        }
    }

    private fun createProducer(): KafkaProducer<String, Order> {
        val producerConfig = Properties()
        // TODO: Get these values from env vars
        producerConfig[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        producerConfig[ProducerConfig.TRANSACTIONAL_ID_CONFIG] = "OrderServiceInstance1"
        producerConfig[ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG] = "true"
        producerConfig[ProducerConfig.RETRIES_CONFIG] = Int.MAX_VALUE.toString()
        producerConfig[ProducerConfig.ACKS_CONFIG] = "all"
        producerConfig[ProducerConfig.CLIENT_ID_CONFIG] = "order-service-producer"
        producerConfig[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = "org.apache.kafka.common.serialization.StringSerializer"
        producerConfig[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = OrderSerializer::class.java
        return KafkaProducer(producerConfig)
    }
}