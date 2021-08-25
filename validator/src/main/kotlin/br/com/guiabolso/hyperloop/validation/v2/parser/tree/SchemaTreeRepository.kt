package br.com.guiabolso.hyperloop.validation.v2.parser.tree

import br.com.guiabolso.hyperloop.schemas.SchemaKey
import br.com.guiabolso.hyperloop.schemas.SchemaRepository
import br.com.guiabolso.hyperloop.validation.PrimitiveTypes
import br.com.guiabolso.hyperloop.validation.PrimitiveTypes.INT
import br.com.guiabolso.hyperloop.validation.PrimitiveTypes.STRING
import br.com.guiabolso.hyperloop.validation.v2.parser.exceptions.TypeNotFoundException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

class SchemaTreeRepository(
    private val schemaRepository: SchemaRepository<String>
) : SchemaRepository<SchemaTree> {

    override fun get(schemaKey: SchemaKey): SchemaTree {
        val rawSchema = schemaRepository.get(schemaKey)
        val schema = yaml.readTree(rawSchema)
        val schemaVersion = schema["schema"]?.get("version")?.intValue()
            ?: throw IllegalArgumentException("Invalid schema. Impossible to detect schema version")

        if (schemaVersion != SUPPORTED_SCHEMA_VERSION) {
            throw IllegalArgumentException("Unsupported schema version: $schemaVersion")
        }

        val types = schema["types"] as? ObjectNode
            ?: throw IllegalStateException("Types should not be empty on schema")
        val validation = schema["validation"] as? ObjectNode
            ?: throw IllegalStateException("Validation should not be empty on schema")

        val nodes = this.createNodes("$", validation, types)
        return SchemaTree(nodes)
    }

    private fun createNodes(nodePath: String, next: ObjectNode, types: ObjectNode): Map<String, ScalarNode> {
        val mapResult = createDefaultNodes(nodePath)
        next.fields().forEach { (key, node) ->
            when {
                node.isScalar() -> mapResult["$nodePath.$key"] = ScalarNode(
                    path = "$nodePath.$key",
                    type = node.type()!!,
                    required = node.isRequired(),
                    nullable = node.isNullable(),
                    encrypted = node.isEncrypted()
                )
                node.isJsonArray() -> mapResult.putAll(
                    createNodes(
                        "$nodePath.$key[*]",
                        nextNode(types, node),
                        types
                    )
                )
                else -> mapResult.putAll(createNodes("$nodePath.$key", nextNode(types, node), types))
            }
        }
        return mapResult
    }

    private fun nextNode(
        types: ObjectNode,
        node: JsonNode
    ) = types[node.rawType()] as? ObjectNode
        ?: throw TypeNotFoundException("Could not find type ${node.rawType()} in schema")

    private fun createDefaultNodes(nodePath: String): MutableMap<String, ScalarNode> =
        when {
            isRoot(nodePath) -> mutableMapOf(
                "$.name" to ScalarNode(
                    path = "$.id",
                    type = STRING,
                    required = true,
                    nullable = false,
                    encrypted = false
                ),
                "$.version" to ScalarNode(
                    path = "$.id",
                    type = INT,
                    required = true,
                    nullable = false,
                    encrypted = false
                ),
                "$.id" to ScalarNode(
                    path = "$.id",
                    type = STRING,
                    required = true,
                    nullable = false,
                    encrypted = false
                ),
                "$.flowId" to ScalarNode(
                    path = "$.flowId",
                    type = STRING,
                    required = true,
                    nullable = false,
                    encrypted = false
                )
            )
            else -> mutableMapOf()
        }

    private fun isRoot(nodePath: String) = nodePath == "$"
    private fun JsonNode.rawType() = this["of"].textValue().replace(arrayTypeRegex) { it.groupValues[1] }
    private fun JsonNode.type() =
        PrimitiveTypes.valueOfOrNull(this.rawType().uppercase())

    private fun JsonNode.isScalar() = this.type() in PrimitiveTypes.values()
    private fun JsonNode.isJsonArray() = this["of"].textValue().matches(arrayTypeRegex)
    private fun JsonNode.isRequired() = TextNode("required") in this.propertyArray()
    private fun JsonNode.isNullable() = TextNode("nullable") in this.propertyArray()
    private fun JsonNode.isEncrypted() = TextNode("encrypted") in this.propertyArray()
    private fun JsonNode.propertyArray() = (this["is"] as? ArrayNode) ?: yaml.createArrayNode()

    companion object {
        private const val SUPPORTED_SCHEMA_VERSION = 2
        private val arrayTypeRegex = "^array\\((.+)\\)\$".toRegex(RegexOption.IGNORE_CASE)
        private val yaml = ObjectMapper(YAMLFactory()).registerKotlinModule()
    }
}
