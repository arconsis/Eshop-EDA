package com.arconsis.domain

import Address
import com.arconsis.data.AddressesRepository
import com.arconsis.presentation.http.dto.CreateAddress
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class AddressesService(private val addressesRepository: AddressesRepository) {

    @Transactional
    fun getAddresses(userId: UUID): List<Address>? {
        return addressesRepository.getAddresses(userId)
    }

    @Transactional
    fun createAddress(createAddress: CreateAddress, userId: UUID): Address {
        return addressesRepository.createAddress(createAddress, userId)
    }
}
