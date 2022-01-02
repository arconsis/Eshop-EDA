package com.arconsis.domain.email

import com.arconsis.common.orderTopicSerde
import com.arconsis.common.userTopicSerde
import com.arconsis.data.email.EmailDto
import com.arconsis.data.email.EmailRepository
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.isOutForShipment
import com.arconsis.domain.orders.isPaid
import com.arconsis.domain.users.User
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.kstream.Joined
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.KTable
import org.eclipse.microprofile.config.inject.ConfigProperty
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class EmailService(
    private val emailRepository: EmailRepository,
    private val usersTable: KTable<String, User>,
    @ConfigProperty(name = "email.sender") private val sender: String,
) {

    fun handleOrderEvents(stream: KStream<String, Order>) {
        stream
            .selectKey { _, order ->
                order.userId.toString()
            }
            .filter { _, order ->
                order.isOutForShipment || order.isPaid
            }
            .join(
                usersTable,
                { order, customer -> Pair(order, customer) },
                Joined.with(Serdes.String(), orderTopicSerde, userTopicSerde)
            )
            .peek { _, (order, customer) ->
                when (order.isPaid) {
                    true -> emailRepository.sendEmail(
                        EmailDto(
                            senderEmail = sender,
                            receiverEmail = customer.email,
                            subject = EMAIL_SUBJECT_ORDER_PAID,
                            text = "New order with number: ${order.orderId} just placed! We will inform you with another email about shipment progress."
                        )
                    )
                    else -> emailRepository.sendEmail(
                        EmailDto(
                            senderEmail = sender,
                            receiverEmail = customer.email,
                            subject = EMAIL_SUBJECT_ORDER_OUT_FOR_SHIPMENT,
                            text = "The order with number: ${order.orderId} is on the way for delivery!"
                        )
                    )
                }
            }
    }

    companion object {
        private const val EMAIL_SUBJECT_ORDER_OUT_FOR_SHIPMENT = "New order was placed!"
        private const val EMAIL_SUBJECT_ORDER_PAID = "New order confirmed!"
    }
}