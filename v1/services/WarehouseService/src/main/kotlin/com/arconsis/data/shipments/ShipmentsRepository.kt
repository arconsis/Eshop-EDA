package com.arconsis.data.shipments

import com.arconsis.domain.shipments.CreateShipment
import com.arconsis.domain.shipments.Shipment
import com.arconsis.domain.shipments.UpdateShipment
import javax.enterprise.context.ApplicationScoped
import javax.persistence.EntityManager

@ApplicationScoped
class ShipmentsRepository(private val entityManager: EntityManager) {

    fun createShipment(createShipment: CreateShipment): Shipment {
        val shipmentEntity = createShipment.toShipmentEntity()
        entityManager.persist(shipmentEntity)
        entityManager.flush()
        return shipmentEntity.toShipment()
    }

    fun updateShipment(updateShipment: UpdateShipment): Shipment {
        val shipmentEntity = entityManager.find(ShipmentEntity::class.java, updateShipment.id)
        shipmentEntity.status = updateShipment.status
        entityManager.merge(shipmentEntity)
        entityManager.flush()
        return shipmentEntity.toShipment()
    }
}