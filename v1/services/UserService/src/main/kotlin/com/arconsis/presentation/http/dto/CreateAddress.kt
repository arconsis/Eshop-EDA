package com.arconsis.presentation.http.dto

import CountryCode
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class CreateAddress(
    @field:NotBlank
    val name: String,
    @field:NotBlank
    val address: String,
    @field:NotBlank
    val houseNumber: String,
    @field:NotNull
    val countryCode: CountryCode,
    @field:NotBlank
    val postalCode: String,
    @field:NotBlank
    val city: String,
    @field:NotBlank
    val phone: String,
)