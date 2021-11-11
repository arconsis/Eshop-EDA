package com.arconsis.data.email

import io.smallrye.mutiny.Uni
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@RegisterRestClient(configKey = "mailgun")
@RegisterClientHeaders(EmailApiHeadersFactory::class)
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
interface EmailApi {
    @POST
    @Path("/messages")
    @Consumes("application/x-www-form-urlencoded")
    fun sendEmail(
        @FormParam("from") senderEmail: String,
        @FormParam("to") receiverEmail: String,
        @FormParam("subject") subject: String,
        @FormParam("text") text: String,
    ): Uni<EmailDispatcherResponse>
}

typealias EmailDispatcherResponse = Response