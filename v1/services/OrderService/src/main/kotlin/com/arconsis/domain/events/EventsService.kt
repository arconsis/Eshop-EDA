package com.arconsis.domain.events

import com.arconsis.common.Message
import com.arconsis.common.OrderStatus
import com.arconsis.data.OrdersRepository
import com.arconsis.domain.orders.CreateOrder
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrderEventType
import com.arconsis.domain.orders.toOrderEvent
import com.arconsis.domain.payments.PaymentEvent
import com.arconsis.domain.payments.PaymentType
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Channel
import org.eclipse.microprofile.reactive.messaging.Emitter
import org.eclipse.microprofile.reactive.messaging.Incoming
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class EventsService(
    @Channel("orders-out") private val emitter: Emitter<Record<String, Message<OrderEventType, Order>>>,
    private val ordersRepository: OrdersRepository,
) {

    fun createOrder(createOrder: CreateOrder): Order {
        val order = ordersRepository.createOrder(createOrder)
        val orderEvent = order.toOrderEvent(OrderEventType.ORDER_CREATED)
        emitter.send(Record.of(orderEvent.key, orderEvent.value)).toCompletableFuture().get()
        return order
    }

    @Incoming("payments-in")
    fun consumePaymentEvents(event: PaymentEvent) {
        val (_, value) = event
        when (value.type) {
            PaymentType.PAYMENT_PROCESSED -> {
                val order = ordersRepository.updateOrder(value.payload.orderId, OrderStatus.PAID)
                val orderEvent = order.toOrderEvent(OrderEventType.ORDER_CONFIRMED)
                emitter.send(Record.of(orderEvent.key, orderEvent.value))
            }
            PaymentType.PAYMENT_FAILED -> {
                ordersRepository.updateOrder(value.payload.orderId, OrderStatus.PAYMENT_FAILED)
            }
        }
    }

    // TODO: handle shipment and warehouse
}
