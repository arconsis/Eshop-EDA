package com.arconsis.domain

import Address
import com.arconsis.data.AddressesRepository
import com.arconsis.presentation.http.dto.CreateAddress
import com.arconsis.presentation.http.dto.CreateBillingAddress
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class AddressesService(private val addressesRepository: AddressesRepository) {

    @Transactional
    fun getAddresses(userId: UUID): List<Address> {
        return addressesRepository.getAddresses(userId)
    }

    @Transactional
    fun createAddress(createAddress: CreateAddress, userId: UUID): Address {
        return addressesRepository.createAddress(createAddress, userId)
    }

    @Transactional
    fun getBillingAddress(userId: UUID): Address {
        return addressesRepository.getBillingAddress(userId)
    }

    @Transactional
    fun createBillingAddress(createBillingAddress: CreateBillingAddress): Address {
        return addressesRepository.createBillingAddress(createBillingAddress)
    }

    @Transactional
    fun deleteBillingAddress(userId: UUID, addressId: UUID): Boolean {
        return addressesRepository.deleteBillingAddress(userId, addressId)
    }
}
