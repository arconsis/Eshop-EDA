package com.arconsis.presentation.http

import Address
import com.arconsis.domain.AddressesService
import com.arconsis.domain.User
import com.arconsis.domain.UsersService
import com.arconsis.presentation.http.dto.AddressCreate
import com.arconsis.presentation.http.dto.UserCreate
import io.smallrye.common.annotation.Blocking
import java.net.URI
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.validation.Valid
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriInfo

@ApplicationScoped
@Path("/users")
class UsersResource(private val usersService: UsersService, private val addressesService: AddressesService) {

    @Blocking
    @GET
    @Path("/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    fun getUser(@PathParam("userId") userId: UUID): User? {
        return usersService.getUser(userId)
    }

    @Blocking
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    fun createUser(@Valid userCreate: UserCreate, uriInfo: UriInfo): Response {
        val createdUser = usersService.createUser(userCreate)
        val path = uriInfo.path
        val location = path + createdUser.id
        return Response.created(URI.create(location)).entity(createdUser).build()
    }

    @Blocking
    @GET
    @Path("/{userId}/addresses")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    fun getAddresses(@PathParam("userId") userId: UUID): List<Address> {
        return addressesService.getAddresses(userId)
    }

    @Blocking
    @POST
    @Path("/{userId}/addresses")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    fun createAddress(
        @PathParam("userId") userId: UUID,
        @Valid addressCreate: AddressCreate,
        uriInfo: UriInfo
    ): Response {
        val createdAddress = addressesService.createAddress(addressCreate, userId)
        val path = uriInfo.path
        val location = path + createdAddress.id
        return Response.created(URI.create(location)).entity(createdAddress).build()
    }
}