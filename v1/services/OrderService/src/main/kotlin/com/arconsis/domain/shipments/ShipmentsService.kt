package com.arconsis.domain.shipments

import com.arconsis.common.retryWithBackoff
import com.arconsis.data.OrdersRepository
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrderStatus
import com.arconsis.domain.orders.toOrderRecord
import com.arconsis.domain.orders.toOrderRecordWithStatus
import io.smallrye.mutiny.Uni
import io.smallrye.reactive.messaging.MutinyEmitter
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Channel
import org.jboss.logging.Logger
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ShipmentsService(
    @Channel("orders-out") private val emitter: MutinyEmitter<Record<String, Order>>,
    private val ordersRepository: OrdersRepository,
    private val logger: Logger
) {
    fun handleShipmentEvents(shipment: Shipment): Uni<Void> {
        return when (shipment.status) {
            ShipmentStatus.DELIVERED -> {
                ordersRepository.updateOrder(shipment.orderId, OrderStatus.COMPLETED)
                    .handleUpdateOrderErrors(shipment.orderId, OrderStatus.COMPLETED)
                    .flatMap { order ->
                        val orderRecord = order.toOrderRecord()
                        emitter.send(orderRecord)
                    }
            }
            ShipmentStatus.SHIPPED -> {
                ordersRepository.updateOrder(shipment.orderId, OrderStatus.SHIPPED)
                    .handleUpdateOrderErrors(shipment.orderId, OrderStatus.SHIPPED)
                    .flatMap { order ->
                        val orderRecord = order.toOrderRecord()
                        emitter.send(orderRecord)
                    }
            }
            ShipmentStatus.FAILED -> {
                ordersRepository.updateOrder(shipment.orderId, OrderStatus.SHIPMENT_FAILED)
                    .handleUpdateOrderErrors(shipment.orderId, OrderStatus.SHIPMENT_FAILED)
                    .flatMap { order ->
                        val orderRecord = order.toOrderRecord()
                        emitter.send(orderRecord)
                    }
            }
            else -> return Uni.createFrom().voidItem()
        }
    }

    private fun Uni<Order>.handleUpdateOrderErrors(orderId: UUID, orderStatus: OrderStatus) = retryWithBackoff()
        .onFailure()
        .call { _ ->
            ordersRepository.getOrder(orderId)
                .flatMap { order ->
                    val orderRecord = order.toOrderRecordWithStatus(status = orderStatus)
                    sendOrderEvent(orderRecord)
                }
        }

    private fun sendOrderEvent(orderRecord: Record<String, Order>): Uni<Void> {
        logger.info("Send order record ${orderRecord.value()}")
        return emitter.send(orderRecord)
    }
}