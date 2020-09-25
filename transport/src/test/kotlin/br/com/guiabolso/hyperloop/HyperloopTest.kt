package br.com.guiabolso.hyperloop

import br.com.guiabolso.events.builder.EventBuilder
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.hyperloop.cryptography.cypher.MessageCypher
import br.com.guiabolso.hyperloop.exceptions.InvalidInputException
import br.com.guiabolso.hyperloop.transport.Transport
import br.com.guiabolso.hyperloop.util.Clock
import br.com.guiabolso.hyperloop.util.isoFormat
import br.com.guiabolso.hyperloop.validation.ValidationResult
import br.com.guiabolso.hyperloop.validation.Validator
import br.com.guiabolso.hyperloop.validation.exceptions.ValidationException
import com.github.salomonbrys.kotson.jsonObject
import com.github.salomonbrys.kotson.set
import com.google.gson.Gson
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import java.util.UUID

class HyperloopTest {

    private lateinit var hyperloop: Hyperloop
    private val transport: Transport = mock()
    private var validator: Validator = mock()
    private var cryptographyEngine: MessageCypher = mock()
    private var clock: Clock = mock()
    private lateinit var event: RequestEvent
    private lateinit var date: LocalDateTime
    private val gson = Gson()

    @Before
    fun setUp() {
        hyperloop = Hyperloop(transport, validator, cryptographyEngine, clock)

        event = EventBuilder.event {
            name = "test:event"
            version = 1
            id = UUID.randomUUID().toString()
            flowId = UUID.randomUUID().toString()
            payload = jsonObject("answer" to 42)
            auth = jsonObject()
            metadata = jsonObject("origin" to "Kyoto - Japan")
        }
        date = LocalDateTime.now()
    }

    @Test
    fun `test can send event as message`() {
        event.metadata["receivedAt"] = date.isoFormat()
        whenever(clock.now()).thenReturn(date)
        whenever(validator.validate(any())).thenReturn(ValidationResult(true, mutableSetOf(), mutableSetOf()))
        whenever(cryptographyEngine.cypher(any())).thenAnswer {
            it.arguments[0] as String
        }

        hyperloop.offer(event)

        verify(transport).sendMessage(gson.toJson(event))
    }

    @Test(expected = ValidationException::class)
    fun `test validate event fails`() {
        whenever(clock.now()).thenReturn(date)
        whenever(validator.validate(any())).thenReturn(
            ValidationResult(
                false,
                mutableSetOf(InvalidInputException("some input was invalid")),
                mutableSetOf()
            )
        )
        whenever(cryptographyEngine.cypher(any())).thenAnswer {
            it.arguments[0] as String
        }

        hyperloop.offer(event)
    }
}
