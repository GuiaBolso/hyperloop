package br.com.guiabolso.hyperloop.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.node.ObjectNode

@JsonIgnoreProperties(ignoreUnknown = true)
data class SchemaData(
    val schema: Schema,
    val event: EventKey,
    val types: ObjectNode?,
    val validation: ObjectNode
)

data class Schema(
    val version: Int
)

data class EventKey(
    val name: String,
    val version: Int
)
