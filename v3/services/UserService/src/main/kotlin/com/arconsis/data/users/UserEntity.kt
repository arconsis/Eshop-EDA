package com.arconsis.data.users

import io.quarkus.security.jpa.Password
import java.time.Instant
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity(name = "users")
class UserEntity(
    @Id
    @GeneratedValue
    var id: UUID? = null,
    @Column(name = "first_name")
    var firstName: String,

    @Column(name = "last_name")
    var lastName: String,

    @Column
    var email: String,

    @Password
    @Column
    var password: String,

    @Column
    var username: String,

    @Column(name = "created_at")
    var createdAt: Instant? = null,

    @Column(name = "updated_at")
    var updatedAt: Instant? = null,
)