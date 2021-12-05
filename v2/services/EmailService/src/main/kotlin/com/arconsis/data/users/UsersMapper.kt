package com.arconsis.data.users

import com.arconsis.domain.users.User

fun UserEntity.toUser(): User {
    return User(
        id = this.id!!,
        userId = this.userId,
        firstName = this.firstName,
        lastName = this.lastName,
        email = this.email,
        username = this.username,
    )
}