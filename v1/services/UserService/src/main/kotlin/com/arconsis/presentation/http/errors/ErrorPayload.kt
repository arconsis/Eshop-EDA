package com.arconsis.presentation.http.errors

import javax.ws.rs.core.Response

data class ErrorPayload (
    var message: String,
    var status: Response.Status,
)