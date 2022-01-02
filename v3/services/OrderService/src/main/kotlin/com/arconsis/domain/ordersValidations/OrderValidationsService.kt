package com.arconsis.domain.ordersValidations

import com.arconsis.common.Topics
import com.arconsis.common.orderSerde
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrderStatus
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.KTable
import org.apache.kafka.streams.kstream.Produced
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrderValidationsService(private val ordersTable: KTable<String, Order>) {

    fun handleOrderValidationEvents(stream: KStream<String, OrderValidation>) {
        stream.filter { _, orderValidation ->
            orderValidation.isValidated || orderValidation.isInvalid
        }
            .join(ordersTable) { orderValidation, order ->
                val updatedOrder = order.copy(status = orderValidation.type.toOrderStatus())
                updatedOrder
            }
            .map { _, order ->
                KeyValue.pair(order.orderId.toString(), order)
            }
            .to(Topics.ORDERS.topicName, Produced.with(Serdes.String(), orderSerde))
    }

    private fun OrderValidationType.toOrderStatus(): OrderStatus = when (this) {
        OrderValidationType.VALIDATED -> OrderStatus.VALIDATED
        OrderValidationType.INVALID -> OrderStatus.OUT_OF_STOCK
    }
}