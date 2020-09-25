package br.com.guiabolso.hyperloop.validation.v1

import br.com.guiabolso.hyperloop.exceptions.WrongSchemaFormatException
import br.com.guiabolso.hyperloop.model.SchemaData
import com.fasterxml.jackson.databind.JsonNode

object SchemaNodeTypeParser {

    private val typeRegex = "^(\\\$?\\w+)(\\((.+)\\))?".toRegex(RegexOption.IGNORE_CASE)

    fun getSchemaNodeType(schema: SchemaData, nodeKey: String, specNode: JsonNode): SchemaType {
        val rawType = specNode.get("of")?.asText() ?: throw WrongSchemaFormatException("Missing type for key $nodeKey")
        val groups = typeRegex.find(rawType)?.groupValues
            ?: throw WrongSchemaFormatException("Illegal type $rawType. $rawType is neither primitive, array, date nor user defined")
        val type = groups[1]
        val attribute = if (groups.size > 1) {
            groups.last()
        } else null
        return getType(
            type,
            nodeKey,
            attribute,
            schema,
            specNode
        )
    }

    private fun getType(type: String, nodeKey: String, attribute: String?, schema: SchemaData, specNode: JsonNode): SchemaType {
        return when (type.toLowerCase()) {
            "string", "long", "int", "float", "double", "boolean" -> PrimitiveType(
                nodeKey,
                type
            )
            "array" -> {
                val arrayContentType = when (attribute) {
                    "string", "long", "int", "float", "double", "boolean" -> PrimitiveType(
                        nodeKey,
                        attribute
                    )
                    "date" -> DateType(nodeKey, attribute)
                    else ->
                        if (isUserDefinedType(
                            attribute.notNull("Array content type should not be null"),
                            schema
                        )
                        ) {
                            UserDefinedType(nodeKey, schema.types!!.get(attribute))
                        } else {
                            throw WrongSchemaFormatException("Illegal type $type. $type is neither primitive, array, date nor user defined")
                        }
                }
                ArrayType(nodeKey, arrayContentType)
            }
            "date" -> DateType(
                nodeKey,
                attribute.notNull("Date type should have one parameter")
            )
            "map" -> {
                val attrs = attribute.notNull("Map content type should not be null").split(",")

                // TODO remove when other key types become accepted
                if (attrs[0] != "string") throw WrongSchemaFormatException("Illegal type $type. Map key must be string")

                val key = getType(
                    attrs[0],
                    nodeKey,
                    attribute,
                    schema,
                    specNode
                )
                val value = getType(
                    attrs[1],
                    nodeKey,
                    attribute,
                    schema,
                    specNode
                )

                MapType(nodeKey, key, value)
            }
            else ->
                if (isUserDefinedType(type, schema)) {
                    UserDefinedType(nodeKey, schema.types!!.get(type))
                } else {
                    throw WrongSchemaFormatException("Illegal type $type. $type is neither primitive, array, date nor user defined")
                }
        }
    }

    private fun isUserDefinedType(type: String, schema: SchemaData): Boolean {
        return schema.types?.get(type) != null
    }

    private fun String?.notNull(message: String) = this ?: throw WrongSchemaFormatException(message)
}
