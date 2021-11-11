package com.arconsis.domain.inventory

import com.arconsis.data.inventory.InventoryRepository
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class InventoryService(
    private val inventoryRepository: InventoryRepository,
) {

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