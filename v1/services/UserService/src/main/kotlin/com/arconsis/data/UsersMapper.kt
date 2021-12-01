package com.arconsis.data

import com.arconsis.domain.User

fun UserEntity.toUser(): User {
    val addressEntities = this.addressEntities
    val addresses = addressEntities?.map { addressEntity -> addressEntity.toAddress() }?.toMutableList()
    return User(
        id = this.id!!,
        firstName = this.firstName,
        lastName = this.lastName,
        email = this.email,
        username = this.username,
        addresses = addresses,
    )
}