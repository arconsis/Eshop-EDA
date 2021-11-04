package com.arconsis.http.orders

import com.arconsis.domain.events.EventsService
import com.arconsis.domain.orders.CreateOrder
import com.arconsis.domain.orders.Order
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.UriInfo

@ApplicationScoped
@Path("/orders")
class OrdersResource(private val eventsService: EventsService) {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    fun createOrder(createOrder: CreateOrder, uriInfo: UriInfo): Order {
        return eventsService.createOrder(createOrder)
    }
}
