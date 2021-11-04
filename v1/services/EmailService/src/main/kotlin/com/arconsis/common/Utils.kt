package com.arconsis.common

import javax.ws.rs.core.MultivaluedMap

fun String?.addHeader(name: String, clientHeaders: MultivaluedMap<String, String>) {
    if (this != null) {
        clientHeaders.putSingle(name, this)
    }
}
