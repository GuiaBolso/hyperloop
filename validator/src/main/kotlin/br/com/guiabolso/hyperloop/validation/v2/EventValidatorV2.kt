package br.com.guiabolso.hyperloop.validation.v2

import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.hyperloop.schemas.SchemaKey
import br.com.guiabolso.hyperloop.schemas.SchemaRepository
import br.com.guiabolso.hyperloop.validation.ValidationResult
import br.com.guiabolso.hyperloop.validation.Validator
import br.com.guiabolso.hyperloop.validation.v2.parser.tree.SchemaTree
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.PathNotFoundException
import com.jayway.jsonpath.spi.json.GsonJsonProvider

class EventValidatorV2(
    private val schemaRepository: SchemaRepository<SchemaTree>
) : Validator {

    override fun validate(event: RequestEvent): ValidationResult {
        val schemaTree = schemaRepository.get(SchemaKey(event.name, event.version))

        val jsonStr = gson.toJson(event)
        val jsonContext = JsonPath.parse(jsonStr, jsonContextConfiguration)

        val validationErrors = validationErrors(schemaTree, jsonContext)
        return ValidationResult(
            validationSuccess = validationErrors.size == 0,
            validationErrors = validationErrors,
            encryptedFields = schemaTree.filter { it.value.encrypted }.map { it.value.path }.toMutableSet()
        )
    }

    private fun validationErrors(schemaTree: SchemaTree, jsonContext: DocumentContext): MutableSet<Throwable> {
        val validationErrors = mutableSetOf<Throwable>()
        for ((jsonPath, node) in schemaTree) {
            try {
                val element = jsonContext.read<JsonElement>(jsonPath)
                node.validate(element)
            } catch (e: PathNotFoundException) {
                if (node.required) {
                    validationErrors.add(e)
                }
            } catch (e: Exception) {
                validationErrors.add(e)
            }
        }
        return validationErrors
    }

    companion object {
        private val gson = GsonBuilder().serializeNulls().create()
        private val jsonContextConfiguration = Configuration
            .builder()
            .jsonProvider(GsonJsonProvider(gson))
            .build()
    }
}
