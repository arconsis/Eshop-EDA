package com.arconsis.data.inventory

import com.arconsis.data.inventory.InventoryEntity.Companion.PRODUCT_ID
import com.arconsis.data.inventory.InventoryEntity.Companion.STOCK
import com.arconsis.domain.inventory.CreateInventory
import com.arconsis.domain.inventory.Inventory
import com.arconsis.domain.inventory.UpdateInventory
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.persistence.EntityManager

@ApplicationScoped
class InventoryRepository(private val entityManager: EntityManager) {

    fun getInventory(id: UUID): Inventory? {
        val inventoryEntity = entityManager.find(InventoryEntity::class.java, id)
        return inventoryEntity?.toInventory()
    }

    fun createInventory(createInventory: CreateInventory): Inventory {
        val inventoryEntity = createInventory.toInventoryEntity()
        entityManager.persist(inventoryEntity)
        entityManager.flush()
        return inventoryEntity.toInventory()
    }

    fun updateInventory(updateInventory: UpdateInventory): Inventory {
        val inventoryEntity = entityManager.find(InventoryEntity::class.java, updateInventory.id)
        inventoryEntity.stock = updateInventory.stock ?: inventoryEntity.stock
        entityManager.merge(inventoryEntity)
        entityManager.flush()
        return inventoryEntity.toInventory()
    }

    fun reserveProductStock(productId: String, stock: Int): Boolean {
        return try {
            entityManager.createNamedQuery(InventoryEntity.UPDATE_PRODUCT_STOCK, InventoryEntity::class.java)
                .setParameter(PRODUCT_ID, productId)
                .setParameter(STOCK, stock)
                .executeUpdate()
            true
        } catch (e: Exception) {
            false
        }
    }
}