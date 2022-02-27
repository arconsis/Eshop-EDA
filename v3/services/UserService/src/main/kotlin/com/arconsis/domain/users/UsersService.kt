package com.arconsis.domain.users

import com.arconsis.data.outboxevents.OutboxEventsRepository
import com.arconsis.data.users.UsersRepository
import com.arconsis.http.dto.UserCreate
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class UsersService(
    private val usersRepository: UsersRepository,
    private val outboxEventsRepository: OutboxEventsRepository,
    private val objectMapper: ObjectMapper
) {

    @Transactional
    fun createUser(userCreate: UserCreate): User {
        val user = usersRepository.createUser(userCreate)
        outboxEventsRepository.createEvent(user.toCreateOutboxEvent(objectMapper))
        return user
    }

    @Transactional
    fun getUser(userId: UUID): User {
        return usersRepository.getUser(userId)
    }
}