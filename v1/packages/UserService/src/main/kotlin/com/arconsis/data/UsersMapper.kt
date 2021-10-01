package com.arconsis.data

import com.arconsis.domain.User

fun UserEntity.toUser(): User {
    return User(
        id = this.id!!,
        firstName = this.firstName,
        lastName = this.lastName,
        email = this.email,
        password = this.password,
        username = this.username,
    )
}
