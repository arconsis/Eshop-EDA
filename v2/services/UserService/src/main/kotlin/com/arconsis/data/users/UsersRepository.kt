package com.arconsis.data.users

import com.arconsis.domain.users.User
import com.arconsis.presentation.http.dto.UserCreate
import io.quarkus.elytron.security.common.BcryptUtil
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class UsersRepository(private val sessionFactory: Mutiny.SessionFactory) {

    fun createUser(userCreate: UserCreate, session: Mutiny.Session): Uni<User> {
        val password = BcryptUtil.bcryptHash(userCreate.password)
        val userEntity = UserEntity(
            firstName = userCreate.firstName,
            lastName = userCreate.lastName,
            email = userCreate.email,
            password = password,
            username = userCreate.username,
        )
        return session.persist(userEntity)
            .map { userEntity.toUser() }
    }

    fun getUser(userId: UUID): Uni<User> {
        return sessionFactory.withTransaction { s, _ ->
            s.find(UserEntity::class.java, userId)
                .map { userEntity -> userEntity.toUser() }
        }
    }
}