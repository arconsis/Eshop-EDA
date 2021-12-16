package com.arconsis.domain.inventory

import io.smallrye.mutiny.Uni
import io.smallrye.reactive.messaging.MutinyEmitter
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Channel
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class InventoryService(
    @Channel("warehouse-out") private val emitter: MutinyEmitter<Record<String, Inventory>>,
) {
    fun createInventory(createInventory: CreateInventory): Uni<Inventory> {
        val inventory = createInventory.toInventory()
        val inventoryRecord = inventory.toInventoryRecord()
        return emitter.send(inventoryRecord).map {
            inventory
        }
    }
}