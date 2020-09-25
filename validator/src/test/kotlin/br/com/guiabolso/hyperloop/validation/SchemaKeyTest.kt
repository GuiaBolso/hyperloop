package br.com.guiabolso.hyperloop.validation

import br.com.guiabolso.hyperloop.schemas.SchemaKey
import org.junit.Assert.assertEquals
import org.junit.Test

class SchemaKeyTest {

    @Test
    fun `test schemaKey has correct name`() {
        val schemaKey = SchemaKey(
            name = "event:test",
            version = 1
        )
        assertEquals("event_test_V1.yaml", schemaKey.toString())
    }
}
