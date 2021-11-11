package com.arconsis.data.users

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class UserDto(
    @JsonProperty("id") val id: UUID,
    @JsonProperty("firstName") val firstName: String,
    @JsonProperty("lastName") val lastName: String,
    @JsonProperty("email") val email: String,
    @JsonProperty("username") val username: String
)