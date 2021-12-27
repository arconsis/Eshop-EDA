package com.arconsis.data

import Address
import AddressEntity
import com.arconsis.presentation.http.dto.CreateAddress
import com.arconsis.presentation.http.dto.CreateBillingAddress
import setBillingAddress
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
            .setParameter("user_id", userId).resultList

        return listOfAddressEntities.map { addressEntity -> addressEntity.toAddress() }
    }

    fun createBillingAddress(createBillingAddress: CreateBillingAddress): Address {
        val addressEntity = entityManager.getReference(AddressEntity::class.java, createBillingAddress.addressId)
        setBillingAddress(addressEntity)

        entityManager.persist(addressEntity)
        entityManager.flush()

        return addressEntity.toAddress()
    }

    fun getBillingAddress(userId: UUID): Address {
        val billingAddressEntity = entityManager.createNamedQuery("get_billing_address", AddressEntity::class.java)
            .setParameter("user_id", userId).singleResult
        return billingAddressEntity.toAddress()
    }

    fun deleteBillingAddress(userId: UUID, addressId: UUID): Boolean {
        val numOfexecutedUpdates = entityManager.createNamedQuery("delete_billing_address", AddressEntity::class.java)
            .setParameter("user_id", userId).executeUpdate()
        return numOfexecutedUpdates != 0
    }
}