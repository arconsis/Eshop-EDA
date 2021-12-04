package com.arconsis.presentation.http

import com.arconsis.domain.users.User
import com.arconsis.domain.users.UsersService
import com.arconsis.presentation.http.dto.UserCreate
import io.smallrye.mutiny.Uni
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.validation.Valid
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.UriInfo

@ApplicationScoped
@Path("/users")
class UserResource(private val usersService: UsersService) {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    fun createUser(@Valid userCreate: UserCreate, uriInfo: UriInfo): Uni<User> {
        return usersService.createUser(userCreate)
    }

    @GET
    @Path("/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    fun getUser(@PathParam("userId") userId: UUID): Uni<User> {
        return usersService.getUser(userId)
    }
}