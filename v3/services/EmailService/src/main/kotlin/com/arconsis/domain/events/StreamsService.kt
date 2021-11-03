package com.arconsis.domain.events

import com.arconsis.common.Topics
import com.arconsis.data.email.EmailDto
import com.arconsis.data.email.EmailRepository
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.isOutForShipment
import com.arconsis.domain.orders.isPaid
import com.arconsis.domain.users.User
import io.quarkus.kafka.client.serialization.ObjectMapperSerde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.Consumed
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Produces

@ApplicationScoped
class StreamsService(
	val emailRepository: EmailRepository
) {

	@Produces
	fun buildTopology(): Topology {
		val builder = StreamsBuilder()
		val orderTopicSerde = ObjectMapperSerde(Order::class.java)
		val userTopicSerde = ObjectMapperSerde(User::class.java)
		val usersTable = builder.table(Topics.USERS.topicName, Consumed.with(Serdes.String(), userTopicSerde))

		builder
			.stream(
				Topics.ORDERS.topicName,
				Consumed.with(Serdes.String(), orderTopicSerde)
			)
			.filter { key, order ->
				order.isOutForShipment || order.isPaid
			}
//			.selectKey { _, order ->
//				order.userId.toString()
//			}
			.join(usersTable) { order, customer ->
				Pair(order, customer)
			}
			.peek { _, (order, customer) ->
				when (order.isPaid) {
					true -> emailRepository.sendEmail(
						EmailDto(
							senderEmail = SENDER_EMAIL,
							receiverEmail = customer.email,
							subject = EMAIL_SUBJECT_ORDER_PAID,
							text = "New order with number: ${order.orderId} just placed! We will inform you with another email about shipment progress."
						)
					)
					else -> emailRepository.sendEmail(
						EmailDto(
							senderEmail = SENDER_EMAIL,
							receiverEmail = customer.email,
							subject = EMAIL_SUBJECT_ORDER_OUT_FOR_SHIPMENT,
							text = "The order with number: ${order.orderId} is on the way for delivery!"
						)
					)
				}
			}

		return builder.build()
	}

	companion object {
		private const val SENDER_EMAIL = "dimosthenis.botsaris@arconsis.com"
		private const val EMAIL_SUBJECT_ORDER_OUT_FOR_SHIPMENT = "New order was placed!"
		private const val EMAIL_SUBJECT_ORDER_PAID = "New order confirmed!"
	}
}