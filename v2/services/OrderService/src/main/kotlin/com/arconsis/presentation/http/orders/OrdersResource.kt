package com.arconsis.presentation.http.orders

import com.arconsis.domain.orders.CreateOrder
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrdersService
import io.smallrye.mutiny.Uni
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.UriInfo

@ApplicationScoped
@Path("/orders")
class OrdersResource(
    private val ordersService: OrdersService,
) {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    fun createOrder(createOrder: CreateOrder, uriInfo: UriInfo): Uni<Order> {
        return ordersService.createOrder(createOrder)
    }

    @GET
    @Path("/{orderId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    suspend fun getOrder(@PathParam("orderId") orderId: UUID): Uni<Order> {
        return ordersService.getOrder(orderId)
    }
}