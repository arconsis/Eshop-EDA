package com.arconsis.utils

import com.arconsis.domain.User
import com.arconsis.domain.UserData
import com.arconsis.http.dto.UserCreate
import java.util.*

fun createUserData(): UserData {
    return UserData(
        id = UUID.fromString("5d1444c4-922e-40ab-81df-8ea5de9a1762"),
        firstName = "Giannis",
        lastName = "Antetokounmpo",
        email = "john.ade@gmail.com",
    )
}

fun createUserCreate(): UserCreate {
    return UserCreate(
        firstName = "Giannis",
        lastName = "Antetokounmpo",
        email = "john.ade@gmail.com",
        password = "giannis",
        username = "greekFreak",
    )
}

fun createTestUser(): User {
    return User(
        id = UUID.fromString("5d1444c4-922e-40ab-81df-8ea5de9a1762"),
        firstName = "Giannis",
        lastName = "Antetokounmpo",
        email = "john.ade@gmail.com",
        password = "giannis",
        username = "greekFreak",
    )
}