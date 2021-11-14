package com.arconsis.data.shipments

import com.arconsis.domain.shipments.CreateShipment
import com.arconsis.domain.shipments.Shipment
import com.arconsis.domain.shipments.UpdateShipment
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ShipmentsRepository(private val sessionFactory: Mutiny.SessionFactory) {

    fun createShipment(createShipment: CreateShipment): Uni<Shipment> {

        val shipmentEntity = createShipment.toShipmentEntity()
        return sessionFactory.withTransaction { s, _ ->
            s.persist(shipmentEntity).map { shipmentEntity.toShipment() }
        }
    }

    fun updateShipment(updateShipment: UpdateShipment): Uni<Shipment> {
        return sessionFactory.withTransaction { s, _ ->
            s.find(ShipmentEntity::class.java, updateShipment.id)
                .onItem().ifNotNull().invoke { entity -> entity.status = updateShipment.status }
                .onItem().ifNotNull().transformToUni { entity -> s.merge(entity) }
                .onItem().transform { it.toShipment() }
        }
    }
}