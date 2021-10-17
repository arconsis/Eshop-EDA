package com.arconsis.domain

import com.arconsis.data.UsersRepository
import com.arconsis.utils.createTestUser
import com.arconsis.utils.createUserCreate
import com.arconsis.utils.createUserData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import java.util.*

@ExtendWith(MockitoExtension::class)
class UsersServiceTest {
    @Mock
    lateinit var usersRepository: UsersRepository

    @InjectMocks
    lateinit var usersService: UsersService

    @Test
    fun shouldCreateUser() {
        //given
        val expectedUserData = createUserData()
        val userCreate = createUserCreate()
        `when`(usersRepository.createUser(userCreate)).thenReturn(expectedUserData)

        //when
        val createdUserData = usersService.createUser(userCreate)

        //then
        verify(usersRepository).createUser(userCreate)
        assertThat(createdUserData).isEqualTo(expectedUserData)
    }

    @Test
    fun shouldReturnSpecificUser() {
        //given
        val userId = UUID.fromString("5d1444c4-922e-40ab-81df-8ea5de9a1762")
        val user = createTestUser()
        `when`(usersRepository.getSpecificUser(userId)).thenReturn(user)

        //when
        val expectedUser = usersService.getSpecificUser(userId)

        //then
        verify(usersRepository).getSpecificUser(userId)
        assertThat(expectedUser).isEqualTo(user)
    }
}