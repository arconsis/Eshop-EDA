package com.arconsis.data.email

import com.arconsis.common.addHeader
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory
import java.util.*
import javax.ws.rs.core.MultivaluedHashMap
import javax.ws.rs.core.MultivaluedMap

class EmailApiHeadersFactory(
    @ConfigProperty(name = "QUARKUS_MAILGUN_API_KEY") private val apiKey: String,
) : ClientHeadersFactory {
    override fun update(
        incomingHeaders: MultivaluedMap<String, String>?,
        clientOutgoingHeaders: MultivaluedMap<String, String>?
    ): MultivaluedMap<String, String> {
        val clientHeaders = MultivaluedHashMap<String, String>()
        val auth = Base64.getEncoder().encodeToString("api:$apiKey".toByteArray())
        "Basic $auth".addHeader("Authorization", clientHeaders)
        return clientHeaders
    }
}