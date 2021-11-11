package com.arconsis.domain.inventory

import java.util.*

open class CreateInventory(val productId: String, val stock: Int)

class Inventory(val id: UUID, productId: String, stock: Int) : CreateInventory(productId, stock)

class UpdateInventory(val id: UUID, val stock: Int?)