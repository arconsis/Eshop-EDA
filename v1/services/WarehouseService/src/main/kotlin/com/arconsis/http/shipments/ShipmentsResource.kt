package com.arconsis.http.shipments

import com.arconsis.domain.shipments.Shipment
import com.arconsis.domain.shipments.ShipmentsService
import com.arconsis.domain.shipments.UpdateShipment
import io.smallrye.common.annotation.Blocking
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.BadRequestException
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.PathParam

@ApplicationScoped
@Path("/shipments")
class ShipmentsResource(private val shipmentsService: ShipmentsService) {

    @PUT
    @Path("/{id}")
    @Blocking
    fun updateShipment(@PathParam("id") id: UUID, updateShipment: UpdateShipment): Shipment {
        if (id != updateShipment.id) {
            throw BadRequestException("Shipment id: $id is not correct")
        }
        return shipmentsService.updateShipment(updateShipment)
    }
}
