package com.arconsis.data.shipments

import com.arconsis.domain.shipments.CreateShipment
import com.arconsis.domain.shipments.Shipment
import com.arconsis.domain.shipments.UpdateShipment
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.coroutines.awaitSuspending
import org.hibernate.reactive.mutiny.Mutiny
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ShipmentsRepository(private val sessionFactory: Mutiny.SessionFactory) {

    fun createShipment(createShipment: CreateShipment, session: Mutiny.Session): Uni<Shipment> {
        val shipmentEntity = createShipment.toShipmentEntity()
        return session.persist(shipmentEntity)
            .map { shipmentEntity.toShipment() }
    }

    fun updateShipment(updateShipment: UpdateShipment, session: Mutiny.Session? = null): Uni<Shipment> {
        return session?.updateShipment(updateShipment) ?: updateShipment(updateShipment)
    }

    private fun Mutiny.Session.updateShipment(updateShipment: UpdateShipment): Uni<Shipment> {
        return this.find(ShipmentEntity::class.java, updateShipment.id)
            .onItem().ifNotNull().invoke { entity -> entity.status = updateShipment.status }
            .onItem().ifNotNull().transformToUni { entity -> this.merge(entity) }
            .map { it.toShipment() }
    }

    private fun updateShipment(updateShipment: UpdateShipment): Uni<Shipment> {
        return sessionFactory.withTransaction { s, _ ->
            s.find(ShipmentEntity::class.java, updateShipment.id)
                .onItem().ifNotNull().invoke { entity -> entity.status = updateShipment.status }
                .onItem().ifNotNull().transformToUni { entity -> s.merge(entity) }
                .map { it.toShipment() }
        }
    }
}