package com.arconsis.domain.events

import com.arconsis.data.OrdersRepository
import com.arconsis.domain.orders.CreateOrder
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrderStatus
import com.arconsis.domain.orders.toOrderRecord
import com.arconsis.domain.ordersvalidations.OrderValidation
import com.arconsis.domain.ordersvalidations.OrderValidationStatus
import com.arconsis.domain.payments.Payment
import com.arconsis.domain.payments.PaymentStatus
import com.arconsis.domain.shipments.Shipment
import com.arconsis.domain.shipments.ShipmentStatus
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
    fun consumePaymentEvents(paymentRecord: Record<String, Payment>) {
        val value = paymentRecord.value()
        when (value.status) {
            PaymentStatus.PROCESSED -> {
                val order = ordersRepository.updateOrder(value.orderId, OrderStatus.PAID)
                val orderRecord = order?.toOrderRecord()
                if (orderRecord != null) {
                    emitter.send(orderRecord)
                }
            }
            PaymentStatus.FAILED -> {
                ordersRepository.updateOrder(value.orderId, OrderStatus.PAYMENT_FAILED)
            }
        }
    }

    @Incoming("shipments-in")
    @Blocking
    @Transactional
    fun consumeShipmentEvents(shipmentRecord: Record<String, Shipment>) {
        val value = shipmentRecord.value()
        when (value.status) {
            ShipmentStatus.SHIPPED -> {
                ordersRepository.updateOrder(value.orderId, OrderStatus.COMPLETED)
            }
            ShipmentStatus.OUT_FOR_SHIPMENT -> {
                ordersRepository.updateOrder(value.orderId, OrderStatus.OUT_FOR_SHIPMENT)
            }
        }
    }

    @Incoming("order-validation-in")
    @Blocking
    @Transactional
    fun consumeOrderValidationEvents(orderValidationRecord: Record<String, OrderValidation>) {
        val value = orderValidationRecord.value()
        when (value.status) {
            OrderValidationStatus.VALID -> {
                val order = ordersRepository.updateOrder(value.orderId, OrderStatus.VALID) ?: return

                val orderRecord = order.toOrderRecord()
                emitter.send(orderRecord).toCompletableFuture().get()

            }
            OrderValidationStatus.INVALID -> {
                // TODO: Do we need to inform the user here about the out of stock ?
                ordersRepository.updateOrder(value.orderId, OrderStatus.OUT_OF_STOCK)
            }
        }
    }
}
