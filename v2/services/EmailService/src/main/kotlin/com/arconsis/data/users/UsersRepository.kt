package com.arconsis.data.users

import com.arconsis.domain.users.User
import com.arconsis.presentation.events.users.CreateUser
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class UsersRepository(private val sessionFactory: Mutiny.SessionFactory) {
    fun createUser(user: CreateUser): Uni<User> {
        return sessionFactory.withTransaction { session, _ ->
            val userEntity = UserEntity(
                userId = user.userId,
                firstName = user.firstName,
                lastName = user.lastName,
                email = user.email,
                username = user.username,
            )
            session.persist(userEntity)
                .map {
                    userEntity.toUser()
                }
        }
    }

    fun getUser(userId: UUID): Uni<User?> {
        return sessionFactory.withTransaction { s, _ ->
            s.createNamedQuery<UserEntity>(UserEntity.GET_USER_BY_USER_ID)
                .setParameter("userId", userId)
                .singleResultOrNull
                .map { userEntity ->
                    userEntity?.toUser()
                }
        }
    }

    fun updateUser(user: CreateUser): Uni<User?> {
        return sessionFactory.withTransaction { s, _ ->
            s.createNamedQuery<UserEntity>(UserEntity.GET_USER_BY_USER_ID)
                .setParameter("userId", user.userId)
                .singleResultOrNull
                .map { userEntity ->
                    if (userEntity == null) {
                        Uni.createFrom().voidItem()
                    }
                    userEntity.firstName = user.firstName
                    userEntity.lastName = user.lastName
                    userEntity.email = user.email
                    userEntity.username = user.username
                    userEntity
                }
                .onItem().ifNotNull().transformToUni { userEntity ->
                    s.merge(userEntity)
                }
                .map { updatedEntity -> updatedEntity.toUser() }
        }
    }
}