package br.com.guiabolso.hyperloop.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.node.ObjectNode

@JsonIgnoreProperties(ignoreUnknown = true)
data class SchemaData(
        val schema: schema,
        val event: event,
        val types: ObjectNode,
        val validation: ObjectNode
)

data class schema(
        val version: Int
)

data class event(
        val name: String,
        val version: Int
)
