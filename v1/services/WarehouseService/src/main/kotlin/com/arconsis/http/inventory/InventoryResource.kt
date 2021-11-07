package com.arconsis.http.inventory

import com.arconsis.domain.inventory.CreateInventory
import com.arconsis.domain.inventory.Inventory
import com.arconsis.domain.inventory.InventoryService
import com.arconsis.domain.inventory.UpdateInventory
import io.smallrye.common.annotation.Blocking
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.*

@ApplicationScoped
@Path("/inventory")
class InventoryResource(private val inventoryService: InventoryService) {

    @GET
    @Blocking
    fun getInventory(id: UUID): Inventory {
        return inventoryService.getInventory(id) ?: throw NotFoundException("Inventory for id: $id not found")
    }

    @POST
    @Blocking
    fun createInventory(createInventory: CreateInventory): Inventory {
        return inventoryService.createInventory(createInventory)
    }

    @PUT
    @Path("/{id}")
    @Blocking
    fun updateInventory(@PathParam("id") id: UUID, updateInventory: UpdateInventory): Inventory {
        if (id != updateInventory.id) {
            throw BadRequestException("Inventory id: $id is not correct")
        }
        return inventoryService.updateInventory(updateInventory)
    }
}