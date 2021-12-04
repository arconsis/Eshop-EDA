package com.arconsis.data.shipments

import com.arconsis.domain.shipments.CreateShipment
import com.arconsis.domain.shipments.Shipment
import com.arconsis.domain.shipments.UpdateShipment
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ShipmentsRepository(private val sessionFactory: Mutiny.SessionFactory) {

    fun createShipment(createShipment: CreateShipment, session: Mutiny.Session): Uni<Shipment> {
        val shipmentEntity = createShipment.toShipmentEntity()
        return session.persist(shipmentEntity)
            .map { shipmentEntity.toShipment() }
    }

    fun updateShipment(updateShipment: UpdateShipment, session: Mutiny.Session): Uni<Shipment> {
        return session.find(ShipmentEntity::class.java, updateShipment.id)
            .onItem().ifNotNull().invoke { entity -> entity.status = updateShipment.status }
            .onItem().ifNotNull().transformToUni { entity -> session.merge(entity) }
            .map { it.toShipment() }
    }
}