package com.arconsis.data.users

import com.arconsis.domain.users.User

data class UserEvent(
    val key: String,
    val value: User
)

fun User.toUserEvent() = UserEvent(
    key = this.id.toString(),
    value = this
)