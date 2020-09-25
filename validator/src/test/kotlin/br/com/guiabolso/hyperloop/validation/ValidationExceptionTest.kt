package br.com.guiabolso.hyperloop.validation

import br.com.guiabolso.hyperloop.validation.exceptions.ValidationException
import org.junit.Assert.assertEquals
import org.junit.Test

class ValidationExceptionTest {

    @Test
    fun `test error message is correct`() {
        val errors = setOf(IllegalArgumentException("some illegal argument"), IllegalStateException("some illegal state"))
        val validationException = ValidationException("some message", errors)

        assertEquals(
            validationException.message,
            """|some message
                                                     |errors:
                                                            |	some illegal argument
                                                            |	some illegal state""".trimMargin()
        )
    }
}
