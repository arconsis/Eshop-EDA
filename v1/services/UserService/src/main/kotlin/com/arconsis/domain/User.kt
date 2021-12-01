package com.arconsis.domain

import Address
import java.util.*

data class User(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val email: String,
    val username: String,
    val addresses: MutableList<Address>?,
)