package com.arconsis.presentation.http.dto

import Address
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

data class CreateUser(
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
    val addresses: List<Address>? = emptyList(),
)