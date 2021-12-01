package com.arconsis.domain

import Address
import com.arconsis.data.AddressesRepository
import com.arconsis.presentation.http.dto.AddressCreate
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
    fun createAddress(addressCreate: AddressCreate, userId: UUID): Address {
        return addressesRepository.createAddress(addressCreate, userId)
    }
}
