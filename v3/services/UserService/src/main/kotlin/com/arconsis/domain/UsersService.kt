package com.arconsis.domain

import com.arconsis.data.UsersRepository
import com.arconsis.data.toUserEvent
import com.arconsis.http.dto.UserCreate
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class UsersService(
    private val usersRepository: UsersRepository,
    private val eventService: EventService,
) {

    @Transactional
    fun createUser(userCreate: UserCreate): User {
        val user = usersRepository.createUser(userCreate)
        val event = user.toUserEvent()
        eventService.sendUserEvent(event)
        return user
    }

    @Transactional
    fun getUser(userId: UUID): User {
        return usersRepository.getUser(userId)
    }
}