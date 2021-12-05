package com.arconsis.common

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.quarkus.jackson.ObjectMapperCustomizer
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class CustomObjectMapper : ObjectMapperCustomizer {
    override fun customize(objectMapper: ObjectMapper) {
        objectMapper.apply {
            // setSerializationInclusion(JsonInclude.Include.NON_NULL)
            registerModule(KotlinModule(nullIsSameAsDefault = true))
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true)
        }
    }
}