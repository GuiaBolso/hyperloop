package br.com.guiabolso.hyperloop

import br.com.guiabolso.hyperloop.schemas.SchemaKey
import br.com.guiabolso.hyperloop.schemas.SchemaRepository
import br.com.guiabolso.hyperloop.validation.PrimitiveTypes
import br.com.guiabolso.hyperloop.validation.v2.parser.exceptions.TypeNotFoundException
import br.com.guiabolso.hyperloop.validation.v2.parser.tree.SchemaTreeRepository
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class SchemaTreeRepositoryTest {

    private lateinit var schemaRepository: SchemaRepository<String>

    @Before
    fun setUp() {
        schemaRepository = mock()
    }

    @Test
    fun `test can find scalar node`() {
        whenever(schemaRepository.get(any())).thenReturn(loadSchemaFromFile("/schema_V2_test.yaml"))

        val schemaTree = SchemaTreeRepository(schemaRepository).get(SchemaKey("test", 1))

        val node = schemaTree["$.metadata.origin"]

        assertEquals(PrimitiveTypes.STRING, node!!.type)
        assertEquals("$.metadata.origin", node.path)
        assertNotNull(schemaTree)
    }

    @Test(expected = TypeNotFoundException::class)
    fun `test cannot find type node`() {
        whenever(schemaRepository.get(any())).thenReturn(loadSchemaFromFile("/schema_V2_broken_type_test.yaml"))
        SchemaTreeRepository(schemaRepository).get(SchemaKey("test", 1))
    }

    @Test
    fun `test can find scalar node of array`() {
        whenever(schemaRepository.get(any())).thenReturn(loadSchemaFromFile("/schema_V2_test.yaml"))

        val schemaTree = SchemaTreeRepository(schemaRepository).get(SchemaKey("test", 1))

        val node = schemaTree["$.payload.array[*].anotherStr"]

        assertNotNull(schemaTree)
        assertNotNull(node)
        assertEquals(PrimitiveTypes.STRING, node!!.type)
        assertEquals("$.payload.array[*].anotherStr", node.path)
    }

    @Test
    fun `test cannot find node`() {
        whenever(schemaRepository.get(any())).thenReturn(loadSchemaFromFile("/schema_V2_test.yaml"))

        val schemaTree = SchemaTreeRepository(schemaRepository).get(SchemaKey("test", 1))

        assertNull(schemaTree["$.identity.jairaoLindao"])
    }

    private fun loadSchemaFromFile(path: String): String {
        return this::class.java.getResourceAsStream(path).bufferedReader().use { it.readText() }
    }
}
