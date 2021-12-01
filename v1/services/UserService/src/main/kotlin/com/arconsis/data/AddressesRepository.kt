package com.arconsis.data

import Address
import AddressEntity
import com.arconsis.presentation.http.dto.AddressCreate
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.persistence.EntityManager

@ApplicationScoped
class AddressesRepository(private val entityManager: EntityManager) {
    fun createAddress(addressCreate: AddressCreate, userId: UUID): Address {
        val userEntity = entityManager.getReference(UserEntity::class.java, userId)

        val addressEntity = AddressEntity(
            firstName = addressCreate.firstName,
            lastName = addressCreate.lastName,
            address = addressCreate.address,
            houseNumber = addressCreate.houseNumber,
            postalCode = addressCreate.postalCode,
            city = addressCreate.city,
            phone = addressCreate.phone,
            userEntity = userEntity,
        )
        entityManager.persist(addressEntity)
        entityManager.flush()

        return addressEntity.toAddress()
    }

    fun getAddresses(userId: UUID): List<Address> {
        val userEntity = entityManager.getReference(UserEntity::class.java, userId)
        val listOfAddressEntities = userEntity.addressEntities

        return listOfAddressEntities!!.map { addressEntity -> addressEntity.toAddress() }
    }

}