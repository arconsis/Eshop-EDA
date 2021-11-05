package com.arconsis.data

import com.arconsis.domain.User
import java.util.*

data class UserEvent(
    val key: String,
    val value: User
)

fun User.toUserEvent() = UserEvent(
    key = this.id.toString(),
    value = this
)
