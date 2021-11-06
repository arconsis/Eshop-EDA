package com.arconsis.domain.events

import com.arconsis.data.PaymentsRepository
import com.arconsis.data.toCreatePayment
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrderStatus
import com.arconsis.domain.payments.Payment
import com.arconsis.domain.payments.PaymentStatus
import com.arconsis.domain.payments.toPaymentRecord
import io.smallrye.reactive.messaging.annotations.Blocking
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Channel
import org.eclipse.microprofile.reactive.messaging.Emitter
import org.eclipse.microprofile.reactive.messaging.Incoming
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class EventsService(
	@Channel("payments-out") private val emitter: Emitter<Record<String, Payment>>,
	private val paymentsRepository: PaymentsRepository,
) {
	@Incoming("orders-in")
	@Blocking
	@Transactional
	fun consumeOrderEvents(orderRecord: Record<String, Order>) {
		val value = orderRecord.value()
		when (value.status) {
			OrderStatus.VALID -> {
				// TODO: simulate API call
				val createPaymentDto = value.toCreatePayment(PaymentStatus.SUCCESS)
				val payment = paymentsRepository.createPayment(createPaymentDto)
				if (payment != null) {
					val paymentRecord = payment.toPaymentRecord()
					emitter.send(paymentRecord)
				}
			}
		}
	}
}