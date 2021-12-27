package com.arconsis.data

import Address
import AddressEntity

fun AddressEntity.toAddress(): Address {
    return Address(
        id = this.id!!,
        name = this.name,
        address = this.address,
        houseNumber = this.houseNumber,
        countryCode = this.countryCode,
        postalCode = this.postalCode,
        city = this.city,
        phone = this.phone,
        isBilling = this.isBilling,
    )
}