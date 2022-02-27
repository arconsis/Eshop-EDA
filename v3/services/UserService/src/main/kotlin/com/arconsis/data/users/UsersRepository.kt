package com.arconsis.data.users

import com.arconsis.domain.users.User
import com.arconsis.http.dto.UserCreate
import io.quarkus.elytron.security.common.BcryptUtil
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.persistence.EntityManager

@ApplicationScoped
class UsersRepository(private val entityManager: EntityManager) {

    fun createUser(userCreate: UserCreate): User {
        val password = BcryptUtil.bcryptHash(userCreate.password)
        val userEntity = UserEntity(
            firstName = userCreate.firstName,
            lastName = userCreate.lastName,
            email = userCreate.email,
            password = password,
            username = userCreate.username,
        )
        entityManager.persist(userEntity)
        entityManager.flush()

        return userEntity.toUser()
    }

    fun getUser(userId: UUID): User {
        val userEntity = entityManager.getReference(UserEntity::class.java, userId)
        return userEntity.toUser()
    }
}