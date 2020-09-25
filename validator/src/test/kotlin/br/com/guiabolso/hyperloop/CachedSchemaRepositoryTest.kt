package br.com.guiabolso.hyperloop

import br.com.guiabolso.hyperloop.schemas.CachedSchemaRepository
import br.com.guiabolso.hyperloop.schemas.SchemaKey
import br.com.guiabolso.hyperloop.schemas.SchemaRepository
import br.com.guiabolso.hyperloop.schemas.exceptions.SchemaFetchingException
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Test

class CachedSchemaRepositoryTest {

    private lateinit var mockSchemaRepository: SchemaRepository<String>
    private lateinit var cachedSchemaRepository: CachedSchemaRepository<String>

    @Before
    fun setUp() {
        mockSchemaRepository = mock()
        cachedSchemaRepository = CachedSchemaRepository(mockSchemaRepository)
    }

    @Test
    fun `can retrieve cached value when repository is called multiple times`() {
        // Given
        val schemaKey = SchemaKey("some-schema", 1)
        // When
        whenever(mockSchemaRepository.get(any())).thenReturn("some schema value")
        cachedSchemaRepository.get(schemaKey)
        // redundant call should be cached
        cachedSchemaRepository.get(schemaKey)
        // Then
        verify(mockSchemaRepository, times(1)).get(any())
    }

    @Test(expected = SchemaFetchingException::class)
    fun `test element is not found`() {
        // Given
        val schemaKey = SchemaKey("some-schema", 1)
        // When
        whenever(mockSchemaRepository.get(any())).thenThrow(SchemaFetchingException::class.java)
        cachedSchemaRepository.get(schemaKey)
    }
}
