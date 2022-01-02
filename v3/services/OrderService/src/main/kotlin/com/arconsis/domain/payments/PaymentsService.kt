package com.arconsis.domain.payments

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
class PaymentsService(private val ordersTable: KTable<String, Order>) {

    fun handlePaymentEvents(stream: KStream<String, Payment>) {
        stream.join(ordersTable) { payment, order ->
            val updatedOrder = order.copy(status = payment.status.toOrderStatus())
            updatedOrder
        }
            .map { _, order ->
                KeyValue.pair(order.orderId.toString(), order)
            }
            .to(Topics.ORDERS.topicName, Produced.with(Serdes.String(), orderSerde))
    }

    private fun PaymentStatus.toOrderStatus(): OrderStatus = when (this) {
        PaymentStatus.SUCCEED -> OrderStatus.PAID
        PaymentStatus.FAILED -> OrderStatus.PAYMENT_FAILED
        PaymentStatus.REFUNDED -> OrderStatus.REFUNDED
    }
}