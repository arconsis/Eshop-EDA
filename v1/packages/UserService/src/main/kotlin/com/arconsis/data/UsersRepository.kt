package com.arconsis.data

import com.arconsis.domain.User
import com.arconsis.http.dto.UserCreate
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.persistence.EntityManager

@ApplicationScoped
class UsersRepository(private val entityManager: EntityManager) {

    fun createUser(userCreate: UserCreate): User {
        val userEntity = UserEntity(
            firstName = userCreate.firstName,
            lastName = userCreate.lastName,
            email = userCreate.email,
            password = userCreate.password,
            username = userCreate.username,
        )
        entityManager.persist(userEntity)
        entityManager.flush()

        return userEntity.toUser()
    }

    fun getSpecificUser(userId: UUID) : User {
         val userEntity = entityManager.getReference(UserEntity::class.java,userId)
        return userEntity.toUser()
    }
}