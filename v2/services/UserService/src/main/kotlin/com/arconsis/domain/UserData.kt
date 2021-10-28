package com.arconsis.domain

import java.util.*

data class UserData(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val email: String,
)