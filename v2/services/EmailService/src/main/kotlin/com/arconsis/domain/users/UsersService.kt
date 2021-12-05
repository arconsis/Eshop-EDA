package com.arconsis.domain.users

import com.arconsis.data.users.UsersRepository
import com.arconsis.presentation.events.users.CreateUser
import io.smallrye.mutiny.Uni
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class UsersService(
    private val usersRepository: UsersRepository,
) {
    fun handleUserEvents(user: CreateUser): Uni<Void> {
        return usersRepository.getUser(user.userId)
            .flatMap { userDoc ->
                if (userDoc == null) {
                    usersRepository.createUser(user)
                } else {
                    usersRepository.updateUser(user)
                }
            }
            .map {
                null
            }
    }
}