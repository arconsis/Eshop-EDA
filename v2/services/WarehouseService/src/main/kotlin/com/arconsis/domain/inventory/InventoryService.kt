package com.arconsis.domain.inventory

import com.arconsis.data.inventory.InventoryRepository
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class InventoryService(
    private val inventoryRepository: InventoryRepository,
) {

    suspend fun getInventory(id: UUID): Inventory? {
        return inventoryRepository.getInventory(id)
    }

    suspend fun createInventory(createInventory: CreateInventory): Inventory {
        return inventoryRepository.createInventory(createInventory)
    }

    suspend fun updateInventory(updateInventory: UpdateInventory): Inventory {
        return inventoryRepository.updateInventory(updateInventory)
    }
}