package com.arconsis.domain.events

import com.arconsis.data.OrdersRepository
import com.arconsis.domain.orders.CreateOrder
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrderStatus
import com.arconsis.domain.orders.toOrderRecord
import com.arconsis.domain.ordersvalidations.OrderValidationMessage
import com.arconsis.domain.ordersvalidations.OrderValidationType
import com.arconsis.domain.payments.PaymentMessage
import com.arconsis.domain.payments.PaymentType
import com.arconsis.domain.shipments.ShipmentMessage
import com.arconsis.domain.shipments.ShipmentType
import io.smallrye.reactive.messaging.annotations.Blocking
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Channel
import org.eclipse.microprofile.reactive.messaging.Emitter
import org.eclipse.microprofile.reactive.messaging.Incoming
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class EventsService(
    @Channel("orders-out") private val emitter: Emitter<Record<String, Order>>,
    private val ordersRepository: OrdersRepository,
) {

    @Transactional
    fun createOrder(createOrder: CreateOrder): Order {
        val order = ordersRepository.createOrder(createOrder)
        val orderRecord = order.toOrderRecord()
        emitter.send(orderRecord).toCompletableFuture().get()
        return order
    }

    @Incoming("payments-in")
    @Blocking
    @Transactional
    fun consumePaymentEvents(paymentRecord: Record<String, PaymentMessage>) {
        val value = paymentRecord.value()
        when (value.type) {
            PaymentType.PAYMENT_PROCESSED -> {
                val order = ordersRepository.updateOrder(value.payload.orderId, OrderStatus.PAID)
                val orderRecord = order?.toOrderRecord()
                if (orderRecord != null) {
                    emitter.send(orderRecord)
                }
            }
            PaymentType.PAYMENT_FAILED -> {
                ordersRepository.updateOrder(value.payload.orderId, OrderStatus.PAYMENT_FAILED)
            }
        }
    }

    @Incoming("shipment-in")
    @Blocking
    @Transactional
    fun consumeShipmentEvents(shipmentRecord: Record<String, ShipmentMessage>) {
        val value = shipmentRecord.value()
        when (value.type) {
            ShipmentType.SHIPMENT_SHIPPED -> {
                ordersRepository.updateOrder(value.payload.orderId, OrderStatus.COMPLETED)
            }
            ShipmentType.SHIPMENT_PREPARED -> {
                ordersRepository.updateOrder(value.payload.orderId, OrderStatus.OUT_FOR_SHIPMENT)
            }
        }
    }

    @Incoming("order-validation-in")
    @Blocking
    @Transactional
    fun consumeOrderValidationEvents(orderValidationRecord: Record<String, OrderValidationMessage>) {
        val value = orderValidationRecord.value()
        when (value.type) {
            OrderValidationType.VALID -> {
                val order = ordersRepository.updateOrder(value.payload.orderId, OrderStatus.VALID) ?: return

                val orderRecord = order.toOrderRecord()
                emitter.send(orderRecord).toCompletableFuture().get()

            }
            OrderValidationType.INVALID -> {
                // TODO: Do we need to inform the user here about the out of stock ?
                ordersRepository.updateOrder(value.payload.orderId, OrderStatus.OUT_OF_STOCK)
            }
        }
    }
}
