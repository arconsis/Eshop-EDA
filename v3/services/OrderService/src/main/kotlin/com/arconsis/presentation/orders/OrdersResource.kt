package com.arconsis.presentation.orders

import com.arconsis.domain.orders.OrdersService
import com.arconsis.presentation.orders.dto.OrderCreateDto
import io.smallrye.common.annotation.Blocking
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.coroutines.awaitSuspending
import java.net.URI
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriInfo

@ApplicationScoped
@Path("/orders")
class OrdersResource(private val ordersService: OrdersService) {
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  suspend fun createUser(orderCreateDto: OrderCreateDto, uriInfo: UriInfo): Response {
    val order = ordersService.createOrder(orderCreateDto)
    val path = uriInfo.path
    val location = path + order.orderNo.toString()
    return Response.created(URI.create(location)).entity(order).build()
  }
}