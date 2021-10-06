package com.arconsis.data

import com.arconsis.domain.User
import com.arconsis.domain.UserData

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

fun UserEntity.toUserData(): UserData {
    return UserData(
        id = this.id!!,
        firstName = this.firstName,
        lastName = this.lastName,
        email = this.email,
    )
}
