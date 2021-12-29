package com.arconsis.data

import AddressEntity
import io.quarkus.security.jpa.Password
import java.time.Instant
import java.util.*
import javax.persistence.*

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

    @OneToMany(mappedBy = "userEntity", cascade = [CascadeType.ALL], orphanRemoval = true)
    var addressEntities: MutableList<AddressEntity> = mutableListOf()
)

fun UserEntity.setAllBillingFlagsFalse() {
    this.addressEntities.map { addressEntity -> addressEntity.isBilling = false }
}

fun UserEntity.checkPreferredList(): Boolean {
    val allowToAddPreferredShippingAddress =
        this.addressEntities.map { addressEntity -> addressEntity.isPreferredShipping = true }.size
    return allowToAddPreferredShippingAddress < 3
}