package br.com.guiabolso.hyperloop.validation

import br.com.guiabolso.events.builder.EventBuilder
import br.com.guiabolso.hyperloop.exceptions.InvalidInputException
import br.com.guiabolso.hyperloop.schemas.SchemaRepository
import br.com.guiabolso.hyperloop.validation.v2.EventValidatorV2
import br.com.guiabolso.hyperloop.validation.v2.parser.tree.SchemaTreeRepository
import com.google.gson.JsonArray
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.jayway.jsonpath.PathNotFoundException
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class EventValidatorV2Test {

    private lateinit var mockSchemaRepository: SchemaRepository<String>
    private lateinit var eventValidator: EventValidatorV2

    private lateinit var schemaV2: String

    @Before
    fun setUp() {
        schemaV2 = loadSchemaFromFile("/schema_V2_test.yaml")
        mockSchemaRepository = mock()
        eventValidator = EventValidatorV2(SchemaTreeRepository(mockSchemaRepository))
    }

    @Test
    fun `test successful validation`() {
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

        whenever(mockSchemaRepository.get(any())).thenReturn(schemaV2)

        val validationResult = eventValidator.validate(event)

        assertTrue(validationResult.validationSuccess)
    }

    @Test
    fun `test fail validation when node is not present`() {
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
            }
            metadata = JsonObject().apply {
                addProperty("origin", "some strange system")
            }
        }

        whenever(mockSchemaRepository.get(any())).thenReturn(schemaV2)

        val validationResult = eventValidator.validate(event)

        assertFalse(validationResult.validationSuccess)
        assertThat(validationResult.validationErrors.iterator().next(), instanceOf(PathNotFoundException::class.java))
    }

    @Test
    fun `test fail validation when node is null`() {
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
                add("origin", JsonNull.INSTANCE)
            }
        }
        whenever(mockSchemaRepository.get(any())).thenReturn(schemaV2)

        val validationResult = eventValidator.validate(event)

        assertFalse(validationResult.validationSuccess)
        assertThat(validationResult.validationErrors.iterator().next(), instanceOf(InvalidInputException::class.java))
    }

    @Test
    fun `test success validation when node is null but is nullable`() {
        val event = EventBuilder.event {
            name = "test"
            version = 1
            flowId = "flow-id"
            id = "id"
            payload = JsonObject().apply {
                addProperty("answer", 42)
                add("nullValue", JsonNull.INSTANCE)
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
                addProperty("origin", "test")
            }
        }
        whenever(mockSchemaRepository.get(any())).thenReturn(schemaV2)

        val validationResult = eventValidator.validate(event)

        assertTrue(validationResult.validationSuccess)
    }

    private fun loadSchemaFromFile(path: String): String {
        return this::class.java.getResourceAsStream(path).bufferedReader().use { it.readText() }
    }
}
