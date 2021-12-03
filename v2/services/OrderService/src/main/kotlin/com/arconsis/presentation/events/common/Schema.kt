package com.arconsis.presentation.events.common

data class Schema(
    val type: String? = null,
    val fields: List<SchemaField>? = null,
    val optional: Boolean? = null,
    val name: String? = null
)

data class SchemaField(
    val type: String? = null,
    val fields: List<FieldField>? = null,
    val optional: Boolean? = null,
    val name: String? = null,
    val field: String? = null
)

data class FieldField(
    val type: String? = null,
    val optional: Boolean? = null,
    val name: String? = null,
    val version: Long? = null,
    val field: String? = null,
    val parameters: Parameters? = null,
    val default: String? = null
)

data class Parameters(
    val allowed: String? = null
)

data class Source(
    val version: String? = null,
    val connector: String? = null,
    val name: String? = null,
    val tsMS: Long? = null,
    val snapshot: String? = null,
    val db: String? = null,
    val sequence: String? = null,
    val schema: String? = null,
    val table: String? = null,
    val txID: Long? = null,
    val lsn: Long? = null,
    val xmin: Any? = null
)