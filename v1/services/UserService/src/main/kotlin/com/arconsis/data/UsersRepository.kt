package com.arconsis.data

import com.arconsis.domain.User
import com.arconsis.presentation.http.dto.CreateUser
import io.quarkus.elytron.security.common.BcryptUtil
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.persistence.EntityManager

@ApplicationScoped
class UsersRepository(private val entityManager: EntityManager) {

    fun createUser(createUser: CreateUser): User {
        val password = BcryptUtil.bcryptHash(createUser.password)
        val userEntity = UserEntity(
            firstName = createUser.firstName,
            lastName = createUser.lastName,
            email = createUser.email,
            password = password,
            username = createUser.username,
        )
        entityManager.persist(userEntity)
        entityManager.flush()

        return userEntity.toUser()
    }

    fun getUser(userId: UUID): User? {
        val userEntity = entityManager.find(UserEntity::class.java, userId) ?: return null
        return userEntity.toUser()
    }
}