package com.arconsis.data.inventory

import com.arconsis.data.inventory.InventoryEntity.Companion.INCREASE_PRODUCT_STOCK
import com.arconsis.data.inventory.InventoryEntity.Companion.PRODUCT_ID
import com.arconsis.data.inventory.InventoryEntity.Companion.STOCK
import com.arconsis.data.inventory.InventoryEntity.Companion.UPDATE_PRODUCT_STOCK
import com.arconsis.domain.inventory.CreateInventory
import com.arconsis.domain.inventory.Inventory
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*
import javax.persistence.*

@NamedQueries(
    NamedQuery(
        name = UPDATE_PRODUCT_STOCK,
        query = """
            update inventory i
            set i.stock = i.stock - :$STOCK
            where i.productId = :$PRODUCT_ID
        """
    ),
    NamedQuery(
        name = INCREASE_PRODUCT_STOCK,
        query = """
            update inventory i
            set i.stock = i.stock + :$STOCK
            where i.productId = :$PRODUCT_ID
        """
    )
)
@Entity(name = "inventory")
class InventoryEntity(
    @Id
    @GeneratedValue
    var id: UUID? = null,

    @Column(name = "product_id", nullable = false)
    var productId: String,

    @Column(nullable = false)
    var stock: Int,

    @CreationTimestamp
    @Column(name = "created_at")
    var createdAt: Instant? = null,

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: Instant? = null,
) {
    companion object {
        const val UPDATE_PRODUCT_STOCK = "InventoryEntity.update_product_stock"
        const val INCREASE_PRODUCT_STOCK = "InventoryEntity.increase_product_stock"
        const val PRODUCT_ID = "product_id"
        const val STOCK = "stock"
    }
}

fun CreateInventory.toInventoryEntity() = InventoryEntity(
    productId = productId,
    stock = stock,
)

fun InventoryEntity.toInventory() = Inventory(
    id = id!!,
    productId = productId,
    stock = stock,
)