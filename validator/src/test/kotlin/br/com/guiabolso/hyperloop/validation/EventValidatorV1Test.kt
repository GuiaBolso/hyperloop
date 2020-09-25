package br.com.guiabolso.hyperloop.validation

import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.hyperloop.exceptions.InvalidInputException
import br.com.guiabolso.hyperloop.schemas.SchemaDataRepository
import br.com.guiabolso.hyperloop.schemas.SchemaKey
import br.com.guiabolso.hyperloop.schemas.SchemaRepository
import br.com.guiabolso.hyperloop.validation.v1.EventValidatorV1
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class EventValidatorV1Test {

    private lateinit var mockSchemaRepository: SchemaRepository<String>
    private lateinit var eventValidator: EventValidatorV1

    private lateinit var schema: String
    private lateinit var schemaWithMap: String
    private lateinit var schemaWithMapStringString: String
    private lateinit var schemaWithEmptyMap: String
    private lateinit var schemaWithNullIdentity: String
    private lateinit var schemaWithNullMetadata: String
    private lateinit var schemaWithNullPayload: String
    private lateinit var schemaWithNullType: String
    private lateinit var schemaWithUserIds: String

    @Before
    fun setUp() {
        schema = loadSchemaFromFile("/schema.yml")
        schemaWithMap = loadSchemaFromFile("/schema-with-map.yml")
        schemaWithMapStringString = loadSchemaFromFile("/string-string-map-schema.yml")
        schemaWithEmptyMap = loadSchemaFromFile("/string-string-map-schema.yml")
        schemaWithNullIdentity = loadSchemaFromFile("/null_identity_schema.yml")
        schemaWithNullMetadata = loadSchemaFromFile("/null_metadata_schema.yml")
        schemaWithNullPayload = loadSchemaFromFile("/null_payload_schema.yml")
        schemaWithNullType = loadSchemaFromFile("/null_type_schema.yml")
        schemaWithUserIds = loadSchemaFromFile("/identity-userIds-schema.yml")

        mockSchemaRepository = mock()
        eventValidator = EventValidatorV1(SchemaDataRepository(mockSchemaRepository))
    }

    @Test
    fun `test successful validation`() {
        val payload =
            """
                        "users": [
                            {
                                "name": "Bruno",
                                "birthdate": "01/01/1990",
                                "gender": "male",
                                "id": 1111111111,
                                "married": false,
                                "height": 2.111,
                                "age": 28,
                                "friend":
                                {
                                    "name": "Carlos",
                                    "birthdate": "22/05/1990",
                                    "gender": "male",
                                    "id": 222222222,
                                    "married": true,
                                    "height": 3.111,
                                    "age": 28
                                }
                            },
                            {
                                "name": "Carlos",
                                "birthdate": "22/05/1990",
                                "gender": "male",
                                "id": 222222222,
                                "married": true,
                                "height": 3.111,
                                "age": 28
                            }
                        ],
                        "file": {
                            "name": "file",
                            "size": 123456.012456398725,
                            "quantity": 1233456877895613,
                            "owner": [
                                {
                                    "name": "Bruno",
                                    "birthdate": "01/01/1990",
                                    "gender": "male",
                                    "id": 1111111111,
                                    "married": false,
                                    "height": 2.111,
                                    "age": 28
                                },
                                {
                                    "name": "Carlos",
                                    "birthdate": "22/05/1990",
                                    "gender": "male",
                                    "id": 222222222,
                                    "married": true,
                                    "height": 3.111,
                                    "age": 28
                                }
                            ]
                        },
                        "name": "Thiago",
                        "x": "Thiago",
                        "y": "Thiago",
                        "o": "Thiago",
                        "k": "Thiago",
                        "l": "Thiago"
            """.trimIndent()

        whenever(mockSchemaRepository.get(SchemaKey("event_test", 1))).thenReturn(schema)

        val response = eventValidator.validate(newEvent("event_test", 1, payload))

        assertTrue(response.validationSuccess)
        assertTrue(response.validationErrors.isEmpty())
        assertTrue(response.encryptedFields.contains("$.payload.users[*].name"))
        assertTrue(response.encryptedFields.contains("$.payload.users[*].friend.name"))
        assertTrue(response.encryptedFields.contains("$.payload.file.name"))
        assertTrue(response.encryptedFields.contains("$.payload.file.quantity"))
        assertTrue(response.encryptedFields.contains("$.identity.userId"))
        assertTrue(response.encryptedFields.contains("$.metadata.origin"))
    }

    @Test
    fun `test successful validation with map`() {
        val payload =
            """
                        "users": [
                            {
                                "name": "Bruno",
                                "birthdate": "01/01/1990",
                                "gender": "male",
                                "id": 1111111111,
                                "married": false,
                                "height": 2.111,
                                "age": 28,
                                "friend":
                                {
                                    "name": "Carlos",
                                    "birthdate": "22/05/1990",
                                    "gender": "male",
                                    "id": 222222222,
                                    "married": true,
                                    "height": 3.111,
                                    "age": 28
                                }
                            },
                            {
                                "name": "Carlos",
                                "birthdate": "22/05/1990",
                                "gender": "male",
                                "id": 222222222,
                                "married": true,
                                "height": 3.111,
                                "age": 28
                            }
                        ],
                        "map": {
                            "first": {
                                    "name": "Carlos",
                                    "birthdate": "22/05/1990",
                                    "gender": "male",
                                    "id": 222222222,
                                    "married": true,
                                    "height": 3.111,
                                    "age": 28
                            },
                            "second": {
                                    "name": "Carlos",
                                    "birthdate": "22/05/1990",
                                    "gender": "male",
                                    "id": 222222222,
                                    "married": true,
                                    "height": 3.111,
                                    "age": 28
                            }
                        },
                        "file": {
                            "name": "file",
                            "size": 123456.012456398725,
                            "quantity": 1233456877895613,
                            "owner": [
                                {
                                    "name": "Bruno",
                                    "birthdate": "01/01/1990",
                                    "gender": "male",
                                    "id": 1111111111,
                                    "married": false,
                                    "height": 2.111,
                                    "age": 28
                                }
                            ]
                        },
                        "name": "Thiago",
                        "x": "Thiago",
                        "y": "Thiago",
                        "o": "Thiago",
                        "k": "Thiago",
                        "l": "Thiago"
            """.trimIndent()

        whenever(mockSchemaRepository.get(SchemaKey("event_test", 1))).thenReturn(schemaWithMap)
        val response = eventValidator.validate(newEvent("event_test", 1, payload))
        assertTrue(response.validationSuccess)
        assertTrue(response.validationErrors.isEmpty())
        assertTrue(response.encryptedFields.contains("$.payload.users[*].name"))
        assertTrue(response.encryptedFields.contains("$.payload.users[*].friend.name"))
        assertTrue(response.encryptedFields.contains("$.payload.file.name"))
        assertTrue(response.encryptedFields.contains("$.payload.file.quantity"))
        assertTrue(response.encryptedFields.contains("$.identity.userId"))
        assertTrue(response.encryptedFields.contains("$.metadata.origin"))
        assertTrue(response.encryptedFields.contains("$.payload.map.name"))
        assertTrue(response.encryptedFields.contains("$.payload.map.birthdate"))
        assertTrue(response.encryptedFields.contains("$.payload.map.height"))
    }

    @Test
    fun `test successful validation with map integer to string`() {
        val payload =
            """
                        "map": {
                            "1": "Bardellinha"
                        }
            """.trimIndent()

        whenever(mockSchemaRepository.get(SchemaKey("event_test", 1))).thenReturn(schemaWithMapStringString)
        val response = eventValidator.validate(newEvent("event_test", 1, payload))
        assertTrue(response.validationSuccess)
    }

    @Test
    fun `test successful validation with empty map`() {
        val payload =
            """
                        "map": {}
            """.trimIndent()

        whenever(mockSchemaRepository.get(SchemaKey("event_test", 1))).thenReturn(schemaWithMapStringString)
        val response = eventValidator.validate(newEvent("event_test", 1, payload))
        assertTrue(response.validationSuccess)
    }

    @Test
    fun `test failed validation with null map`() {
        val payload =
            """
                        "map": null
            """.trimIndent()

        whenever(mockSchemaRepository.get(SchemaKey("event_test", 1))).thenReturn(schemaWithMapStringString)

        val response = eventValidator.validate(newEvent("event_test", 1, payload))

        assertFalse(response.validationSuccess)
        assertTrue(response.encryptedFields.contains("$.identity.userId"))
        assertTrue(response.encryptedFields.contains("$.metadata.origin"))
    }

    @Test
    fun `test successful validation with null types `() {
        val payload =
            """
                        "name": "Thiago",
                        "x": "Thiago",
                        "y": "Thiago"
            """.trimIndent()
        whenever(mockSchemaRepository.get(SchemaKey("event_test", 1))).thenReturn(schemaWithNullType)
        val response = eventValidator.validate(newEvent("event_test", 1, payload))
        assertTrue(response.validationSuccess)
        assertTrue(response.validationErrors.isEmpty())
        assertTrue(response.encryptedFields.isEmpty())
    }

    @Test
    fun `test event with payload but no payload specification`() {
        val payload =
            """
                        "name": "Thiago"
            """.trimIndent()
        val event = newEvent("event_test", 1, payload)
        whenever(mockSchemaRepository.get(SchemaKey("event_test", 1))).thenReturn(schemaWithNullPayload)
        val expectedErrors = mutableListOf<Throwable>(
            InvalidInputException("Event has non-empty payload but the schema has no specification")
        )
        val response = eventValidator.validate(event)

        assertFalse(response.validationSuccess)
        assertTrue(expectedErrors[0].message == response.validationErrors.elementAt(0).message)
    }

    @Test
    fun `test event with userId but no identity specification`() {
        val payload =
            """
                        "name": "Thiago"
            """.trimIndent()
        val event = newEvent("event_test", 1, payload)

        whenever(mockSchemaRepository.get(SchemaKey("event_test", 1))).thenReturn(schemaWithNullIdentity)

        val response = eventValidator.validate(event)

        assertTrue(response.validationSuccess)
    }

    @Test
    fun `test successfull validation with userId and userIds`() {
        val identity =
            """
                        "userId": 1,
                        "userIds": [1, 2, 3]
            """.trimIndent()
        val event = newEvent("event_test", 1, "", identity)

        whenever(mockSchemaRepository.get(SchemaKey("event_test", 1))).thenReturn(schemaWithUserIds)

        val response = eventValidator.validate(event)

        assertTrue(response.validationSuccess)
    }

    @Test
    fun `test event with userId as non JsonPrimitive`() {
        val payload =
            """
                        "name": "Thiago"
            """.trimIndent()
        val identity =
            """
                        "userId": {}
            """.trimIndent()
        val event = newEvent("event_test", 1, payload, identity)

        whenever(mockSchemaRepository.get(SchemaKey("event_test", 1))).thenReturn(schemaWithNullIdentity)
        val expectedErrors = mutableListOf<Throwable>(
            InvalidInputException("Element 'userId' must be a JsonPrimitive")
        )
        val response = eventValidator.validate(event)

        assertFalse(response.validationSuccess)
        assertTrue(expectedErrors[0].message == response.validationErrors.elementAt(0).message)
    }

    @Test
    fun `test event with userIds as non JsonArray`() {
        val payload =
            """
                        "name": "Thiago"
            """.trimIndent()
        val identity =
            """
                        "userIds": 2
            """.trimIndent()
        val event = newEvent("event_test", 1, payload, identity)

        whenever(mockSchemaRepository.get(SchemaKey("event_test", 1))).thenReturn(schemaWithNullIdentity)
        val expectedErrors = mutableListOf<Throwable>(
            InvalidInputException("Element 'userIds' must be a JsonArray")
        )
        val response = eventValidator.validate(event)

        assertFalse(response.validationSuccess)
        assertTrue(expectedErrors[0].message == response.validationErrors.elementAt(0).message)
    }

    @Test
    fun `test event with origin but no metadata specification`() {
        val payload =
            """
                        "name": "Thiago"
            """.trimIndent()
        val event = newEvent("event_test", 1, payload)

        whenever(mockSchemaRepository.get(SchemaKey("event_test", 1))).thenReturn(schemaWithNullMetadata)

        val response = eventValidator.validate(event)

        assertTrue(response.validationSuccess)
    }

    @Test
    fun `test event with no userId nor origin`() {
        val event = newEvent("event_test", 1, "")
        event.metadata.remove("origin")
        event.identity.remove("userId")
        whenever(mockSchemaRepository.get(SchemaKey("event_test", 1))).thenReturn(schemaWithNullType)
        val expectedErrors = mutableListOf<Throwable>(
            InvalidInputException("Element 'name' is required"),
            InvalidInputException("Identity must have 'userId' or 'userIds'"),
            InvalidInputException("Element 'userId' is required"),
            InvalidInputException("Element 'origin' is required")
        )
        val response = eventValidator.validate(event)

        assertFalse(response.validationSuccess)
        assertTrue(expectedErrors[0].message == response.validationErrors.elementAt(0).message)
        assertTrue(expectedErrors[1].message == response.validationErrors.elementAt(1).message)
        assertTrue(expectedErrors[2].message == response.validationErrors.elementAt(2).message)
        assertTrue(expectedErrors[3].message == response.validationErrors.elementAt(3).message)
    }

    @Test
    fun `test wrong array type validation`() {
        val payload =
            """
                        "users": "notAnArray"
            """.trimIndent()
        whenever(mockSchemaRepository.get(SchemaKey("event_test", 1))).thenReturn(schema)
        val expectedErrors = mutableListOf<Throwable>(
            InvalidInputException("Array element 'users' is in the wrong format"),
            InvalidInputException("Element 'name' is required")
        )
        val response = eventValidator.validate(newEvent("event_test", 1, payload))

        assertFalse(response.validationSuccess)
        assertTrue(expectedErrors.first().message == response.validationErrors.first().message)
        assertTrue(expectedErrors.last().message == response.validationErrors.last().message)
    }

    @Test
    fun `test with event name different from schema`() {
        whenever(mockSchemaRepository.get(SchemaKey("xpto", 1))).thenReturn(schema)
        val response = eventValidator.validate(newEvent("xpto", 1, ""))
        assertFalse(response.validationSuccess)
        assertTrue(response.validationErrors.first().message == "The event name 'xpto' is different from schema 'event_test'")
    }

    @Test
    fun `test with event version different from schema`() {
        whenever(mockSchemaRepository.get(SchemaKey("event_test", 3))).thenReturn(schema)
        val response = eventValidator.validate(newEvent("event_test", 3, ""))
        assertFalse(response.validationSuccess)
        assertTrue(response.validationErrors.first().message == "The event version '3' is different from schema '1'")
    }

    private fun newEvent(
        eventName: String,
        eventVersion: Int,
        payload: String,
        identity: String? =
            """ "userId": 1 """
    ): RequestEvent {

        val metadata = JsonObject()
        metadata.addProperty("origin", "origin")

        return RequestEvent(
            eventName,
            eventVersion,
            "Id",
            "flowId",
            JsonParser().parse(
                """{
                        $payload
                }"""
            ),
            JsonParser().parse(
                """{
                        $identity
                }"""
            ).asJsonObject,
            JsonObject(),
            metadata
        )
    }

    private fun loadSchemaFromFile(path: String): String {
        return this::class.java.getResourceAsStream(path).bufferedReader().use { it.readText() }
    }
}
