package com.arconsis.presentation.http.dto

import java.util.*
import javax.validation.constraints.NotBlank

data class CreateBillingAddress(
    @NotBlank
    val addressId: UUID,
    @NotBlank
    val userId: UUID,
)