package com.arconsis.presentation.http.dto

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

data class UserCreate(
    @field:NotBlank
    val firstName: String,
    @field:NotBlank
    val lastName: String,
    @field:NotBlank
    @field:Email
    val email: String,
    @field:NotBlank
    val password: String,
    @field:NotBlank
    val username: String,
)