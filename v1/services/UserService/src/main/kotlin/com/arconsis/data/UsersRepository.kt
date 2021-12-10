package com.arconsis.data

import com.arconsis.domain.User
import com.arconsis.presentation.http.dto.CreateUser
import io.quarkus.elytron.security.common.BcryptUtil
import java.io.IOException
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.persistence.EntityManager
import javax.ws.rs.ClientErrorException
import javax.ws.rs.NotFoundException
import javax.ws.rs.core.Response

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
        val userEntity = entityManager.find(UserEntity::class.java, userId) ?: throw ClientErrorException(Response.Status.NOT_FOUND)
        return userEntity.toUser()
    }
}