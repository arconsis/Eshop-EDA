package com.arconsis.domain

import com.arconsis.data.UsersRepository
import com.arconsis.http.dto.UserCreate
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class UsersService(private val usersRepository: UsersRepository) {

    @Transactional
    fun createUser(userCreate: UserCreate): UserData {
        return usersRepository.createUser(userCreate)
    }

    @Transactional
    fun getSpecificUser(userId: UUID): User {
        return usersRepository.getSpecificUser(userId)
    }
}