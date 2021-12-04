package com.arconsis.data.inventory

import com.arconsis.data.inventory.InventoryEntity.Companion.PRODUCT_ID
import com.arconsis.data.inventory.InventoryEntity.Companion.STOCK
import com.arconsis.domain.inventory.CreateInventory
import com.arconsis.domain.inventory.Inventory
import com.arconsis.domain.inventory.UpdateInventory
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.coroutines.awaitSuspending
import org.hibernate.reactive.mutiny.Mutiny
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class InventoryRepository(private val sessionFactory: Mutiny.SessionFactory) {

    suspend fun getInventory(id: UUID): Inventory? {
        val inventoryEntity = sessionFactory.withTransaction { s, _ ->
            s.find(InventoryEntity::class.java, id)
        }.awaitSuspending()
        return inventoryEntity?.toInventory()
    }

    suspend fun createInventory(createInventory: CreateInventory): Inventory {
        val inventoryEntity = createInventory.toInventoryEntity()
        sessionFactory.withTransaction { s, _ ->
            s.persist(inventoryEntity)
        }.awaitSuspending()

        return inventoryEntity.toInventory()
    }

    suspend fun updateInventory(updateInventory: UpdateInventory): Inventory {
        val inventoryEntity = sessionFactory.withTransaction { s, _ ->
            s.find(InventoryEntity::class.java, updateInventory.id)
                .onItem().ifNotNull().invoke { entity -> entity.stock = updateInventory.stock ?: entity.stock }
                .onItem().ifNotNull().transformToUni { entity -> s.merge(entity) }
        }.awaitSuspending()
        return inventoryEntity.toInventory()
    }

    fun reserveProductStock(productId: String, stock: Int, session: Mutiny.Session): Uni<Boolean> {
        return session.createNamedQuery<InventoryEntity>(InventoryEntity.UPDATE_PRODUCT_STOCK)
            .setParameter(PRODUCT_ID, productId)
            .setParameter(STOCK, stock)
            .executeUpdate()
            .map { updatedRows -> updatedRows == 1 }
            // TODO: Check if we need to handle only the update stock constraint error here
            .onFailure().recoverWithItem(false)
    }

    fun increaseProductStock(productId: String, stock: Int): Uni<Boolean> {
        return sessionFactory.withTransaction { s, _ ->
            s.createNamedQuery<InventoryEntity>(InventoryEntity.UPDATE_PRODUCT_STOCK)
                .setParameter(PRODUCT_ID, productId)
                .setParameter(STOCK, stock)
                .executeUpdate()
                .map { updatedRows -> updatedRows == 1 }
                // TODO: Check if we need to handle only the update stock constraint error here
                .onFailure().recoverWithItem(false)
        }
    }
}