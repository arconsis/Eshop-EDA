package com.arconsis.presentation.http.inventory

import com.arconsis.domain.inventory.CreateInventory
import com.arconsis.domain.inventory.Inventory
import com.arconsis.domain.inventory.InventoryService
import io.smallrye.mutiny.Uni
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.POST
import javax.ws.rs.Path

@ApplicationScoped
@Path("/inventory")
class InventoryResource(private val inventoryService: InventoryService) {
    @POST
    fun createInventory(createInventory: CreateInventory): Uni<Inventory> = inventoryService.createInventory(createInventory)
}