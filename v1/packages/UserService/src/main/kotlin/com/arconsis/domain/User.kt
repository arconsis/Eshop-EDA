package com.arconsis.domain

import java.time.Instant
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.validation.constraints.Email

@ApplicationScoped
data class User (
    val id: UUID,
    val firstName: String,
    val lastName: String,
    @Email
    val email: String,
    val password: String,
    val username: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    )
