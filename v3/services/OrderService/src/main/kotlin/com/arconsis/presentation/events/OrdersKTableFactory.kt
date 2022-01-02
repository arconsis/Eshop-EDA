package com.arconsis.presentation.events


import com.arconsis.common.Topics
import com.arconsis.common.orderSerde
import com.arconsis.domain.orders.Order
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KTable
import javax.enterprise.inject.Produces
import javax.inject.Singleton

class OrdersKTableFactory {

    @Singleton
    @Produces
    fun createOrdersKTable(builder: StreamsBuilder): KTable<String, Order> {
        return builder.table(Topics.ORDERS.topicName, Consumed.with(Serdes.String(), orderSerde))
    }
}