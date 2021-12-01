package com.arconsis.data

import Address
import AddressEntity

fun AddressEntity.toAddress(): Address {
    return Address(
        id = this.id!!,
        firstName = this.firstName,
        lastName = this.lastName,
        address = this.address,
        houseNumber = this.houseNumber,
        postalCode = this.postalCode,
        city = this.city,
        phone = this.phone,
    )
}