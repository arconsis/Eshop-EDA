package com.arconsis.data

import com.arconsis.data.InventoryEntity.Companion.PRODUCT_ID
import com.arconsis.data.InventoryEntity.Companion.STOCK
import com.arconsis.data.InventoryEntity.Companion.UPDATE_PRODUCT_STOCK
import com.arconsis.domain.inventory.CreateInventory
import com.arconsis.domain.inventory.Inventory
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*
import javax.persistence.*

//@NamedQueries(
//    NamedQuery(
//        name = UPDATE_PRODUCT_STOCK,
//        query = """
//            UPDATE inventory i
//            SET i.stock = :$STOCK
//            WHERE i.productId = :$PRODUCT_ID
//        """
//    )
//)
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
        const val PRODUCT_ID = "InventoryEntity.product_id"
        const val STOCK = "InventoryEntity.stock"
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