package com.arconsis.data.users

import com.arconsis.common.body
import com.arconsis.domain.users.User
import io.smallrye.mutiny.coroutines.awaitSuspending
import org.eclipse.microprofile.rest.client.inject.RestClient
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.core.Response

@ApplicationScoped
class UsersRepository(
    @RestClient val usersApi: UsersApi
) {
    suspend fun getUser(userId: UUID): User? {
        return try {
            usersApi.getUser(userId).awaitSuspending().mapToUserResponse()
        } catch (e: Exception) {
            null
        }
    }

    private fun Response.mapToUserResponse(): User? {
        return when (status) {
            200 -> body<UserDto>()?.toUser() ?: return null
            else -> return null
        }
    }
}