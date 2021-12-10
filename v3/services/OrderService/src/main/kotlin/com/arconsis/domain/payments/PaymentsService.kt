package com.arconsis.domain.payments

import com.arconsis.common.Topics
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrderStatus
import io.quarkus.kafka.client.serialization.ObjectMapperSerde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.KTable
import org.apache.kafka.streams.kstream.Produced
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PaymentsService {
    fun handlePaymentEvents(
        stream: KStream<String, Payment>,
        ordersTable: KTable<String, Order>,
        orderSerde: ObjectMapperSerde<Order>
    ) = stream.filter { _, payment -> payment.isSuccess }
        .join(ordersTable) { _, order ->
            order
        }
        .map { _, orderValidation ->
            val paidOrder = orderValidation.copy(status = OrderStatus.PAID)
            KeyValue.pair(paidOrder.userId.toString(), paidOrder)
        }
        .to(Topics.ORDERS.topicName, Produced.with(Serdes.String(), orderSerde))
}