package com.arconsis.presentation.http.errors

import org.jboss.resteasy.reactive.server.ServerExceptionMapper
import javax.validation.ConstraintViolationException
import javax.ws.rs.NotFoundException
import javax.ws.rs.ServerErrorException
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.Response

class AppExceptionMapper {
    @ServerExceptionMapper
    fun mapNotFoundException(exception: NotFoundException): Response {
        return mapException(exception)
    }

    @ServerExceptionMapper
    fun mapValidationException(exception: ConstraintViolationException): Response {
        val listOfViolations = exception.constraintViolations.toList()
        val propertySeparator = ": "
        val message =
            listOfViolations.map { violation -> "${violation.propertyPath}$propertySeparator${violation.message}" }
                .joinToString()
        val errorPayload = ErrorPayload(message, Response.Status.BAD_REQUEST)
        return Response.status(Response.Status.BAD_REQUEST).entity(errorPayload).build()
    }

    @ServerExceptionMapper
    fun mapException(exception: Exception): Response {
        val isWebAppException: Boolean = exception is WebApplicationException
        val isServerErrorException: Boolean = exception is ServerErrorException

        println(exception.message)
        if (isWebAppException) {
            val response = (exception as WebApplicationException).response
            val status = Response.Status.fromStatusCode(response.status)

            if (isServerErrorException) {
                val errorPayload = ErrorPayload(message = "Something Went Wrong", status = status)
                return Response.status(errorPayload.status).entity(errorPayload).build()
            }

            val errorPayload = ErrorPayload(message = exception.message.toString(), status = status)
            return Response.status(errorPayload.status).entity(errorPayload).build()
        }
        val payload = ErrorPayload(message = "Something went wrong", status = Response.Status.INTERNAL_SERVER_ERROR)
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(payload).build()
    }
}