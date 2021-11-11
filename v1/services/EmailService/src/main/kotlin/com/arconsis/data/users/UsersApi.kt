package com.arconsis.data.users

import io.smallrye.mutiny.Uni
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@RegisterRestClient(configKey = "users-api")
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
interface UsersApi {
	@GET
	@Path("/{userId}")
	@Consumes(MediaType.APPLICATION_JSON)
	fun getUser(
		@PathParam("userId") userId: UUID
	): Uni<UsersResponse>
}

typealias UsersResponse = Response