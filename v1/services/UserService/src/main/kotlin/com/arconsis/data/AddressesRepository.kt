package com.arconsis.data

import Address
import AddressEntity
import com.arconsis.data.common.USER_ID
import com.arconsis.presentation.http.dto.CreateAddress
import setAsBillingAddress
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
        val listOfAddressEntities =
            entityManager.createNamedQuery(AddressEntity.LIST_USER_ADDRESSES, AddressEntity::class.java)
                .setParameter("user_id", userId).resultList

        return listOfAddressEntities.map { addressEntity -> addressEntity.toAddress() }
    }

    fun createBillingAddress(userId: UUID, addressId: UUID): Address {
        val userEntity = entityManager.getReference(UserEntity::class.java, userId)
        userEntity.setAllBillingFlagsFalse()
        val addressEntity = entityManager.getReference(AddressEntity::class.java, addressId)
        addressEntity.setAsBillingAddress()
        entityManager.persist(addressEntity)
        entityManager.flush()

        return addressEntity.toAddress()
    }

    fun getBillingAddress(userId: UUID): Address? {
        val billingAddressEntity =
            try {
                entityManager.createNamedQuery(AddressEntity.GET_BILLING_ADDRESS, AddressEntity::class.java)
                    .setParameter(USER_ID, userId).singleResult
            } catch (e: Exception) {
                return null
            }
        return billingAddressEntity.toAddress()
    }

    fun deleteBillingAddress(userId: UUID, addressId: UUID): Boolean {
        val numOfExecutedUpdates = entityManager.createNamedQuery(AddressEntity.DELETE_BILLING_ADDRESS)
            .setParameter("user_id", userId).executeUpdate()
        return numOfExecutedUpdates != 0
    }
}