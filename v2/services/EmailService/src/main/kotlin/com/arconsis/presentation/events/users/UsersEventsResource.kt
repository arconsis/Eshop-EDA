package com.arconsis.presentation.events.users

import com.arconsis.domain.users.User
import com.arconsis.domain.users.UsersService
import com.fasterxml.jackson.databind.ObjectMapper
import io.smallrye.mutiny.Uni
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Incoming
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class UsersEventsResource(
    private val usersService: UsersService,
    private val objectMapper: ObjectMapper
) {
    @Incoming("users-in")
    fun consumeUserEvents(userRecord: Record<String, UserEventDto>): Uni<Void> {
        val userEventDto = userRecord.value()
        val user = objectMapper.readValue(userEventDto.payload.currentValue.toOutboxEvent().payload, CreateUser::class.java)
        return usersService.handleUserEvents(user).onFailure()
            .recoverWithNull()
    }
}