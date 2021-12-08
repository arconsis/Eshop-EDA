package com.arconsis.data

import Address
import AddressEntity
import com.arconsis.presentation.http.dto.CreateAddress
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.persistence.EntityManager

@ApplicationScoped
class AddressesRepository(private val entityManager: EntityManager) {
    fun createAddress(createAddress: CreateAddress, userId: UUID): Address {
        val userEntity = entityManager.getReference(UserEntity::class.java, userId)

        val addressEntity = AddressEntity(
            name = createAddress.name,
            address = createAddress.address,
            houseNumber = createAddress.houseNumber,
            countryCode = createAddress.countryCode,
            postalCode = createAddress.postalCode,
            city = createAddress.city,
            phone = createAddress.phone,
            userEntity = userEntity,
        )
        entityManager.persist(addressEntity)
        entityManager.flush()

        return addressEntity.toAddress()
    }

    fun getAddresses(userId: UUID): List<Address> {
        val listOfAddressEntities = entityManager.createNamedQuery("list_user_addresses", AddressEntity::class.java)
            .setParameter("user_id", userId ).resultList

        return listOfAddressEntities.map { addressEntity -> addressEntity.toAddress() }
    }

}