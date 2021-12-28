package com.arconsis.data

import Address
import AddressEntity
import com.arconsis.presentation.http.dto.CreateAddress
import setBillingAddress
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.persistence.EntityManager
import javax.ws.rs.NotFoundException

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
        setAllBillingFlagsFalse(userEntity)
        val addressEntity = entityManager.getReference(AddressEntity::class.java, addressId)
        setBillingAddress(addressEntity)
        entityManager.persist(addressEntity)
        entityManager.flush()

        return addressEntity.toAddress()
    }

    fun getBillingAddress(userId: UUID): Address {
        val billingAddressEntity =
            try {
                entityManager.createNamedQuery(AddressEntity.GET_BILLING_ADDRESS, AddressEntity::class.java)
                    .setParameter("user_id", userId).singleResult
            } catch (e: Exception) {
                throw NotFoundException("Billing address for user with id: $userId not found")
            }
        return billingAddressEntity.toAddress()
    }

    fun deleteBillingAddress(userId: UUID, addressId: UUID): Boolean {
        val numOfExecutedUpdates = entityManager.createNamedQuery(AddressEntity.DELETE_BILLING_ADDRESS)
            .setParameter("user_id", userId).executeUpdate()
        return numOfExecutedUpdates != 0
    }
}