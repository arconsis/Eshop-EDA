package com.arconsis.domain.inventory

import com.arconsis.data.InventoryRepository
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrderStatus
import com.arconsis.domain.ordersvalidations.OrderValidation
import com.arconsis.domain.ordersvalidations.OrderValidationStatus
import io.smallrye.reactive.messaging.annotations.Blocking
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Channel
import org.eclipse.microprofile.reactive.messaging.Emitter
import org.eclipse.microprofile.reactive.messaging.Incoming
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class InventoryService(
    @Channel("order-validation-out") private val emitter: Emitter<Record<String, OrderValidation>>,
    private val inventoryRepository: InventoryRepository,
) {

    @Incoming("order-in")
    @Blocking
    @Transactional
    fun consumeOrderEvents(orderRecord: Record<String, Order>) {
        val order = orderRecord.value()

        if (order.status !== OrderStatus.PENDING) {
            return
        }

        val stockUpdated = inventoryRepository.reserveProductStock(order.productId, order.quantity)

        val orderValidation = OrderValidation(
            productId = order.productId,
            quantity = order.quantity,
            orderId = order.orderId,
            userId = order.userId,
            status = if (stockUpdated) OrderValidationStatus.VALID else OrderValidationStatus.INVALID
        )

        emitter.send(Record.of(order.userId.toString(), orderValidation))
    }

    @Transactional
    fun getInventory(id: UUID): Inventory? {
        return inventoryRepository.getInventory(id)
    }

    @Transactional
    fun createInventory(createInventory: CreateInventory): Inventory {
        return inventoryRepository.createInventory(createInventory)
    }

    @Transactional
    fun updateInventory(updateInventory: UpdateInventory): Inventory {
        return inventoryRepository.updateInventory(updateInventory)
    }
}
