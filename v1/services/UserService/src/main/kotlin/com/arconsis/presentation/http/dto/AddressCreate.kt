package com.arconsis.presentation.http.dto

import javax.validation.constraints.NotBlank

data class AddressCreate(
    @field:NotBlank
    val firstName: String,
    @field:NotBlank
    val lastName: String,
    @field:NotBlank
    val address: String,
    @field:NotBlank
    val houseNumber: String,
    @field:NotBlank
    val postalCode: String,
    @field:NotBlank
    val city: String,
    @field:NotBlank
    val phone: String,
)