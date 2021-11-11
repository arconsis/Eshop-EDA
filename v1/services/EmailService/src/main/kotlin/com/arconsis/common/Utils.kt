package com.arconsis.common

import javax.ws.rs.core.MultivaluedMap
import javax.ws.rs.core.Response

fun String?.addHeader(name: String, clientHeaders: MultivaluedMap<String, String>) {
    if (this != null) {
        clientHeaders.putSingle(name, this)
    }
}

inline fun <reified T> Response.body(statusCodeRange: IntRange = 200..299, onError: (Response) -> T? = { null }): T? = when (this.status) {
    in statusCodeRange -> {
        bufferEntity()
        readEntity(T::class.java)
    }
    else -> onError(this)
}