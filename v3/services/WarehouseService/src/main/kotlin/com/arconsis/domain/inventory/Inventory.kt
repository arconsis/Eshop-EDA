package com.arconsis.domain.inventory

import io.smallrye.reactive.messaging.kafka.Record
import java.util.*

open class CreateInventory(val productId: String, val stock: Int)

class Inventory(val id: UUID, productId: String, stock: Int) : CreateInventory(productId, stock)

class UpdateInventory(val id: UUID, val stock: Int?)

fun Inventory.toInventoryRecord(): Record<String, Inventory> = Record.of(
    id.toString(),
    this
)

fun CreateInventory.toInventory() = Inventory(
    id = UUID.randomUUID(),
    productId = productId,
    stock = stock
)