package com.arconsis.presentation.http.orders

import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrdersService
import com.arconsis.presentation.http.orders.dto.CreateOrderDto
import com.arconsis.presentation.http.orders.dto.toCreateOrder
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
    suspend fun createOrder(createOrder: CreateOrderDto, uriInfo: UriInfo): Order {
        return ordersService.createOrder(createOrder.toCreateOrder())
    }

    @GET
    @Path("/{orderId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    suspend fun getOrder(@PathParam("orderId") orderId: UUID): Order {
        return ordersService.getOrder(orderId)
    }
}