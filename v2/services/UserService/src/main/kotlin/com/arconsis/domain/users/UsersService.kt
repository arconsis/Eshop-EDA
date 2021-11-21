package com.arconsis.domain.users

import com.arconsis.data.outboxevents.OutboxEventsRepository
import com.arconsis.data.users.UsersRepository
import com.arconsis.presentation.http.dto.UserCreate
import com.fasterxml.jackson.databind.ObjectMapper
import io.smallrye.mutiny.Uni
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class UsersService(
    private val usersRepository: UsersRepository,
    private val outboxEventsRepository: OutboxEventsRepository,
    private val objectMapper: ObjectMapper,
) {

    fun createUser(userCreate: UserCreate): Uni<User> {
        return usersRepository.createUser(userCreate)
            .createOutboxEvent()
    }

    fun getUser(userId: UUID): Uni<User> {
        return usersRepository.getUser(userId)
    }

    private fun Uni<User>.createOutboxEvent() = flatMap { user ->
        val createOutboxEvent = user.toCreateOutboxEvent(objectMapper)
        outboxEventsRepository.createEvent(createOutboxEvent).map {
            user
        }
    }
}