package com.arconsis.presentation.http.dto

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

data class UserCreate(
    @NotBlank
    val firstName: String,
    @NotBlank
    val lastName: String,
    @NotBlank
    @Email
    val email: String,
    @NotBlank
    val password: String,
    @NotBlank
    val username: String,
)