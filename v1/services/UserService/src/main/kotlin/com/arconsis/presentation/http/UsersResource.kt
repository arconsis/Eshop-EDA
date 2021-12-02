package com.arconsis.presentation.http

import Address
import com.arconsis.domain.AddressesService
import com.arconsis.domain.User
import com.arconsis.domain.UsersService
import com.arconsis.presentation.http.dto.CreateAddress
import com.arconsis.presentation.http.dto.CreateUser
import io.smallrye.common.annotation.Blocking
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.validation.Valid
import javax.ws.rs.*
import javax.ws.rs.core.MediaType


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
    fun createUser(@Valid createUser: CreateUser): User {
        return usersService.createUser(createUser)

    }

    @Blocking
    @GET
    @Path("/{userId}/addresses")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    fun getAddresses(@PathParam("userId") userId: UUID): List<Address>? {
        return addressesService.getAddresses(userId)
    }

    @Blocking
    @POST
    @Path("/{userId}/addresses")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    fun createAddress(
        @PathParam("userId") userId: UUID,
        @Valid createAddress: CreateAddress
    ): Address {
        return addressesService.createAddress(createAddress, userId)

    }
}