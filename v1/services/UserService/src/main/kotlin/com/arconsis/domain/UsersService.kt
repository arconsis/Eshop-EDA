package com.arconsis.domain

import com.arconsis.data.UserEvent
import com.arconsis.data.UsersRepository
import com.arconsis.data.toUserEvent
import com.arconsis.presentation.http.dto.UserCreate
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Channel
import org.eclipse.microprofile.reactive.messaging.Emitter
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class UsersService(
    private val usersRepository: UsersRepository,
    @Channel("users-out") private val emitter: Emitter<Record<String, User>>
) {

    @Transactional
    fun createUser(userCreate: UserCreate): User {
        val user = usersRepository.createUser(userCreate)
        val event = user.toUserEvent()
        sendUserEvent(event)
        return user
    }

    @Transactional
    fun getUser(userId: UUID): User? {
        return usersRepository.getUser(userId)
    }

    private fun sendUserEvent(event: UserEvent) {
        print { "Send user record ${event.value}" }
        emitter.send(Record.of(event.key, event.value)).toCompletableFuture().get()
    }
}