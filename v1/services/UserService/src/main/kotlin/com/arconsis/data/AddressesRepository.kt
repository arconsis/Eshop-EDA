package com.arconsis.data

import Address
import AddressEntity
import com.arconsis.data.common.ADDRESS_ID
import com.arconsis.data.common.USER_ID
import com.arconsis.presentation.http.dto.CreateAddress
import setAsBillingAddress
import setAsPreferredShippingAddress
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.persistence.EntityManager
import javax.ws.rs.NotFoundException

@ApplicationScoped
class AddressesRepository(private val entityManager: EntityManager) {
    fun createAddress(createAddress: CreateAddress, userId: UUID): Address {
        val userEntity = entityManager.find(UserEntity::class.java, userId)
            ?: throw NotFoundException("User with id: $userId doesn't exists")
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
        val userEntity = entityManager.find(UserEntity::class.java, userId)
            ?: throw NotFoundException("User with id: $userId doesn't exists")
        val listOfAddressEntities =
            entityManager.createNamedQuery(AddressEntity.LIST_USER_ADDRESSES, AddressEntity::class.java)
                .setParameter(USER_ID, userId).resultList

        return listOfAddressEntities.map { addressEntity -> addressEntity.toAddress() }
    }

    fun createBillingAddress(userId: UUID, addressId: UUID): Address {
        val userEntity = entityManager.find(UserEntity::class.java, userId)
            ?: throw NotFoundException("User with id: $userId doesn't exists")
        userEntity.setAllBillingFlagsFalse()

        val addressEntity = entityManager.getReference(AddressEntity::class.java, addressId)
        addressEntity.setAsBillingAddress()
        entityManager.persist(addressEntity)
        entityManager.flush()

        return addressEntity.toAddress()
    }

    fun getBillingAddress(userId: UUID): Address? {
        val userEntity = entityManager.find(UserEntity::class.java, userId)
            ?: throw NotFoundException("User with id: $userId doesn't exists")
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
            .setParameter(USER_ID, userId).executeUpdate()
        return numOfExecutedUpdates != 0
    }

    fun getPreferredShippingAddresses(userId: UUID): List<Address> {
        val userEntity = entityManager.find(UserEntity::class.java, userId)
            ?: throw NotFoundException("User with id: $userId doesn't exists")
        val preferredShippingAddressEntities =
            entityManager.createNamedQuery(AddressEntity.GET_PREFERRED_SHIPPING_ADDRESSES, AddressEntity::class.java)
                .setParameter(USER_ID, userId).resultList

        return preferredShippingAddressEntities.map { addressEntity -> addressEntity.toAddress() }
    }

    fun createPreferredShippingAddress(userId: UUID, addressId: UUID): Address? {
        val numOfPreferredShippingAddresses =
            entityManager.createNamedQuery(AddressEntity.GET_PREFERRED_SHIPPING_ADDRESSES, AddressEntity::class.java)
                .setParameter(USER_ID, userId).resultList.size
        return if (numOfPreferredShippingAddresses < 3) {
            try {
                val addressEntity = entityManager.getReference(AddressEntity::class.java, addressId)

                addressEntity.setAsPreferredShippingAddress()

                entityManager.persist(addressEntity)
                entityManager.flush()

                addressEntity.toAddress()
            } catch (e: Exception) {
                throw NotFoundException("Address id: $addressId doesn't exist")
            }
        } else null
    }

    fun deletePreferredShippingAddress(userId: UUID, addressId: UUID): Boolean {
        val userEntity = entityManager.find(UserEntity::class.java, userId)
            ?: throw NotFoundException("User with id: $userId doesn't exists")
        val numOfExecutedUpdates = entityManager.createNamedQuery(AddressEntity.DELETE_PREFERRED_SHIPPING_ADDRESS)
            .setParameter(USER_ID, userId)
            .setParameter(ADDRESS_ID, addressId)
            .executeUpdate()

        return numOfExecutedUpdates != 0
    }
}