package com.arconsis.domain.payments

import com.arconsis.common.Topics
import com.arconsis.data.PaymentsRepository
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.isValidated
import com.arconsis.domain.orders.toCreatePayment
import io.quarkus.kafka.client.serialization.ObjectMapperSerde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.Produced
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PaymentsService(val paymentsRepository: PaymentsRepository) {
    fun handleOrderEvents(stream: KStream<String, Order>, paymentTopicSerde: ObjectMapperSerde<Payment>) =
        stream.filter { _, order -> order.isValidated }
            .mapValues { order ->
                paymentsRepository.createPayment(order.toCreatePayment()).await().indefinitely()
                val event = order.toPaymentEvent(PaymentStatus.SUCCEED)
                event.value
            }.to(Topics.PAYMENTS.topicName, Produced.with(Serdes.String(), paymentTopicSerde))
}