package br.com.guiabolso.hyperloop.validation.v1

import br.com.guiabolso.hyperloop.validation.PrimitiveTypes
import com.fasterxml.jackson.databind.JsonNode

sealed class SchemaType {
    abstract val nodeKey: String
}

class ArrayType(
    override val nodeKey: String,
    val contentType: SchemaType
) : SchemaType()

class DateType(
    override val nodeKey: String,
    val format: String
) : SchemaType()

class UserDefinedType(
    override val nodeKey: String,
    val userType: JsonNode
) : SchemaType()

class MapType(
    override val nodeKey: String,
    val key: SchemaType,
    val value: SchemaType
) : SchemaType()

class PrimitiveType(
    override val nodeKey: String,
    rawType: String
) : SchemaType() {
    val type = PrimitiveTypes.valueOf(rawType.toUpperCase())
}
