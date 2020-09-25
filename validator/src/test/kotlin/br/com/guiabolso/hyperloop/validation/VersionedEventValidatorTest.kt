package br.com.guiabolso.hyperloop.validation

import br.com.guiabolso.events.builder.EventBuilder
import br.com.guiabolso.hyperloop.schemas.SchemaRepository
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class VersionedEventValidatorTest {

    private lateinit var versionedEventValidator: VersionedEventValidator
    private lateinit var schemaRepository: SchemaRepository<String>

    @Before
    fun setUp() {
        schemaRepository = mock()
        versionedEventValidator = VersionedEventValidator(schemaRepository)
    }

    @Test
    fun `test can validate schema v1`() {
        val event = EventBuilder.event {
            name = "test"
            version = 1
            flowId = "flow-id"
            id = "id"
            payload = JsonObject().apply {
                addProperty("answer", 42)
                add("nested", JsonObject().apply { addProperty("someStr", "wow such code") })
                add(
                    "array",
                    JsonArray().apply {
                        add(JsonObject().apply { addProperty("anotherStr", "wow such string") })
                        add(JsonObject().apply { addProperty("anotherStr", "wow another string") })
                    }
                )
                add(
                    "arrayPrimitive",
                    JsonArray().apply {
                        add(10)
                        add(20)
                        add(12)
                    }
                )
            }
            identity = JsonObject().apply {
                addProperty("userId", 1)
            }
            metadata = JsonObject().apply {
                addProperty("origin", "some strange system")
            }
        }

        whenever(schemaRepository.get(any())).thenReturn(loadSchemaFromFile("/schema_V1_test.yaml"))

        val validationResult = versionedEventValidator.validate(event)
        assertTrue(validationResult.validationSuccess)
    }

    @Test
    fun `test can validate schema v2`() {
        val event = EventBuilder.event {
            name = "test"
            version = 1
            flowId = "flow-id"
            id = "id"
            payload = JsonObject().apply {
                addProperty("answer", 42)
                addProperty("nullValue", "test")
                add("nested", JsonObject().apply { addProperty("someStr", "wow such code") })
                add(
                    "array",
                    JsonArray().apply {
                        add(JsonObject().apply { addProperty("anotherStr", "wow such string") })
                        add(JsonObject().apply { addProperty("anotherStr", "wow another string") })
                    }
                )
                add(
                    "arrayPrimitive",
                    JsonArray().apply {
                        add(10)
                        add(20)
                        add(12)
                    }
                )
            }
            metadata = JsonObject().apply {
                addProperty("origin", "some strange system")
            }
        }

        whenever(schemaRepository.get(any())).thenReturn(loadSchemaFromFile("/schema_V2_test.yaml"))

        val validationResult = versionedEventValidator.validate(event)
        assertTrue(validationResult.validationSuccess)
    }

    private fun loadSchemaFromFile(path: String): String {
        return this::class.java.getResourceAsStream(path).bufferedReader().use { it.readText() }
    }
}
