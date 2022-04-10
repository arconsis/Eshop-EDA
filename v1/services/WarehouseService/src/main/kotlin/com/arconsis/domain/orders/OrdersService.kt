package com.arconsis.domain.orders

import com.arconsis.common.retryWithBackoff
import com.arconsis.data.inventory.InventoryRepository
import com.arconsis.data.shipments.ShipmentsRepository
import com.arconsis.domain.ordersvalidations.OrderValidation
import com.arconsis.domain.ordersvalidations.OrderValidationStatus
import com.arconsis.domain.shipments.*
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.coroutines.awaitSuspending
import io.smallrye.reactive.messaging.MutinyEmitter
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Channel
import org.jboss.logging.Logger
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrdersService(
    @Channel("shipment-out") private val shipmentEmitter: MutinyEmitter<Record<String, Shipment>>,
    @Channel("order-validation-out") private val orderValidationEmitter: MutinyEmitter<Record<String, OrderValidation>>,
    private val shipmentsRepository: ShipmentsRepository,
    private val inventoryRepository: InventoryRepository,
    private val logger: Logger
) {
    suspend fun handleOrderEvents(order: Order) {
        return when (order.status) {
            OrderStatus.REQUESTED -> handleOrderPending(order)
            OrderStatus.PAID -> handleOrderPaid(order)
            OrderStatus.PAYMENT_FAILED -> handleOrderPaymentFailed(order)
            else -> return
        }
    }

    private suspend fun handleOrderPending(order: Order) {
        val stockUpdated = inventoryRepository.reserveProductStock(order.productId, order.quantity)
        val orderValidation = OrderValidation(
            productId = order.productId,
            quantity = order.quantity,
            orderId = order.id,
            userId = order.userId,
            status = if (stockUpdated) OrderValidationStatus.VALIDATED else OrderValidationStatus.INVALID
        )
        orderValidationEmitter.send(Record.of(order.id.toString(), orderValidation)).awaitSuspending()
    }

    private suspend fun handleOrderPaid(order: Order) {
        runCatching {
            val shipment = retryWithBackoff {
                shipmentsRepository.createShipment(
                    CreateShipment(
                        orderId = order.id,
                        userId = order.userId,
                        status = ShipmentStatus.PREPARING_SHIPMENT
                    )
                )
            }

            val updatedShipment = updateShipment(shipment)
            sendShipmentEvent(updatedShipment.toShipmentRecord())
        }.getOrElse {
            logger.error(it)
            handleShipmentError(order, null)
        }
    }

    private suspend fun handleOrderPaymentFailed(order: Order) {
        return retryWithBackoff { inventoryRepository.increaseProductStock(order.productId, order.quantity) }
    }

    private suspend fun updateShipment(shipment: Shipment): Shipment {
        if (shipment.id == null) {
            throw Exception("Shipment failed")
        }
        return shipmentsRepository.updateShipment(
            UpdateShipment(
                shipment.id,
                ShipmentStatus.SHIPPED
            )
        )
    }

    private suspend fun handleShipmentError(order: Order, shipmentId: UUID?) {
        sendShipmentEvent(order.toFailedShipment(shipmentId).toShipmentRecord())
    }

    private suspend fun sendShipmentEvent(shipmentRecord: Record<String, Shipment>) {
        logger.info("Send shipment record $shipmentRecord")
        shipmentEmitter.send(shipmentRecord).awaitSuspending()
    }
}