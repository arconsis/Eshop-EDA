package com.arconsis.domain.users

import com.arconsis.data.outboxevents.OutboxEventsRepository
import com.arconsis.data.users.UsersRepository
import com.arconsis.presentation.http.dto.UserCreate
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class UsersService(
    private val usersRepository: UsersRepository,
    private val outboxEventsRepository: OutboxEventsRepository,
    private val sessionFactory: Mutiny.SessionFactory,
) {

    fun createUser(userCreate: UserCreate): Uni<User> {
        return sessionFactory.withTransaction { session, _ ->
            usersRepository.createUser(userCreate, session)
                .createOutboxEvent(session)
        }
    }

    fun getUser(userId: UUID): Uni<User> {
        return usersRepository.getUser(userId)
    }

    private fun Uni<User>.createOutboxEvent(session: Mutiny.Session) = flatMap { user ->
        outboxEventsRepository.createEvent(user, session)
            .map {
                user
            }
    }
}