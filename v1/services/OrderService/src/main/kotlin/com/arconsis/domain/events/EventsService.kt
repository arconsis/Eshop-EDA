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
import io.smallrye.mutiny.Uni
import io.smallrye.reactive.messaging.MutinyEmitter
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Channel
import org.eclipse.microprofile.reactive.messaging.Incoming
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class EventsService(
    @Channel("orders-out") private val emitter: MutinyEmitter<Record<String, Order>>,
    private val ordersRepository: OrdersRepository,
) {

    fun createOrder(createOrder: CreateOrder): Uni<Order> {
        return ordersRepository.createOrder(createOrder)
            .onItem().transformToUni { order ->
                val orderRecord = order.toOrderRecord()
                emitter.send(orderRecord).onItem().transform {
                    order
                }
            }
    }

    @Incoming("payments-in")
    fun consumePaymentEvents(paymentRecord: Record<String, Payment>): Uni<Void> {
        val value = paymentRecord.value()
        return when (value.status) {
            PaymentStatus.SUCCESS -> {
                ordersRepository.updateOrder(value.orderId, OrderStatus.PAID).onItem().transformToUni { order ->
                    val orderRecord = order.toOrderRecord()
                    emitter.send(orderRecord)
                }

            }
            PaymentStatus.FAILED -> {
                ordersRepository.updateOrder(value.orderId, OrderStatus.PAYMENT_FAILED).replaceWithVoid()
            }
        }
    }

    @Incoming("shipments-in")
    fun consumeShipmentEvents(shipmentRecord: Record<String, Shipment>): Uni<Void> {
        val value = shipmentRecord.value()
        return when (value.status) {
            ShipmentStatus.SHIPPED -> {
                ordersRepository.updateOrder(value.orderId, OrderStatus.COMPLETED).onItem().transformToUni { order ->
                    val orderRecord = order.toOrderRecord()
                    emitter.send(orderRecord)
                }

            }
            ShipmentStatus.OUT_FOR_SHIPMENT -> {
                ordersRepository.updateOrder(value.orderId, OrderStatus.OUT_FOR_SHIPMENT).onItem()
                    .transformToUni { order ->
                        val orderRecord = order.toOrderRecord()
                        emitter.send(orderRecord)
                    }
            }
            else -> return Uni.createFrom().voidItem()
        }
    }

    @Incoming("order-validation-in")
    fun consumeOrderValidationEvents(orderValidationRecord: Record<String, OrderValidation>): Uni<Void> {
        val value = orderValidationRecord.value()
        return when (value.status) {
            OrderValidationStatus.VALID -> {
                ordersRepository.updateOrder(value.orderId, OrderStatus.VALID).onItem().transformToUni { order ->
                    val orderRecord = order.toOrderRecord()
                    emitter.send(orderRecord)
                }
            }
            OrderValidationStatus.INVALID -> {
                // TODO: Do we need to inform the user here about the out of stock ?
                ordersRepository.updateOrder(value.orderId, OrderStatus.OUT_OF_STOCK).replaceWithVoid()
            }
        }
    }
}