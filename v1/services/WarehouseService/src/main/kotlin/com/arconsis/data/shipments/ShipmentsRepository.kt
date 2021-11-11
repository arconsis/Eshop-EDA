package com.arconsis.data.shipments

import com.arconsis.domain.shipments.CreateShipment
import com.arconsis.domain.shipments.Shipment
import com.arconsis.domain.shipments.UpdateShipment
import io.smallrye.mutiny.coroutines.awaitSuspending
import org.hibernate.reactive.mutiny.Mutiny
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ShipmentsRepository(private val sessionFactory: Mutiny.SessionFactory) {

    suspend fun createShipment(createShipment: CreateShipment): Shipment {

        val shipmentEntity = createShipment.toShipmentEntity()
        sessionFactory.withTransaction { s, _ ->
            s.persist(shipmentEntity)
        }.awaitSuspending()

        return shipmentEntity.toShipment()
    }

    suspend fun updateShipment(updateShipment: UpdateShipment): Shipment {
        val shipmentEntity = sessionFactory.withTransaction { s, _ ->
            s.find(ShipmentEntity::class.java, updateShipment.id)
                .onItem().ifNotNull().invoke { entity -> entity.status = updateShipment.status }
                .onItem().ifNotNull().transformToUni { entity -> s.merge(entity) }
        }.awaitSuspending()

        return shipmentEntity.toShipment()
    }
}