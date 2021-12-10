package com.arconsis.domain.ordersValidations

import com.arconsis.common.Topics
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrderStatus
import io.quarkus.kafka.client.serialization.ObjectMapperSerde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.KTable
import org.apache.kafka.streams.kstream.Produced
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrderValidationsService {
    fun handleOrderValidationEvents(
        stream: KStream<String, OrderValidation>,
        ordersTable: KTable<String, Order>,
        orderSerde: ObjectMapperSerde<Order>
    ) = stream.filter { _, orderValidation ->
        orderValidation.isValidated
    }
        .join(ordersTable) { _, order ->
            order
        }
        .mapValues { orderValidation ->
            val order = orderValidation.copy(status = OrderStatus.VALIDATED)
            order
        }
        .to(Topics.ORDERS.topicName, Produced.with(Serdes.String(), orderSerde))
}