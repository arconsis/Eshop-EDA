package com.arconsis.http

import com.arconsis.domain.User
import com.arconsis.domain.UsersService
import com.arconsis.http.dto.UserCreate
import io.smallrye.common.annotation.Blocking
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.validation.Valid
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.UriInfo

@ApplicationScoped
@Path("/users")
class UserResource(private val usersService: UsersService) {

  @Blocking
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  fun createUser(@Valid userCreate: UserCreate, uriInfo: UriInfo): User {
    return usersService.createUser(userCreate)
  }

  @Blocking
  @GET
  @Path("/{userId}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  fun getUser(@PathParam("userId") userId: UUID): User {
    return usersService.getUser(userId)
  }
}