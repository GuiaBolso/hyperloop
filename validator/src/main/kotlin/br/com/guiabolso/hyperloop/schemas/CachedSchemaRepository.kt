package br.com.guiabolso.hyperloop.schemas

import br.com.guiabolso.hyperloop.schemas.exceptions.SchemaFetchingException
import com.github.benmanes.caffeine.cache.Caffeine
import java.util.concurrent.TimeUnit

class CachedSchemaRepository<R>
@JvmOverloads
constructor(
    private val schemaRepository: SchemaRepository<R>,
    maximumCacheSize: Long = 200L,
    expirationInMinutes: Long = 60L
) : SchemaRepository<R> {

    private val schemaCache = Caffeine.newBuilder()
        .maximumSize(maximumCacheSize)
        .expireAfterWrite(expirationInMinutes, TimeUnit.MINUTES)
        .build<SchemaKey, R> { key ->
            retrieveSchema(key)
        }

    override fun get(schemaKey: SchemaKey) = schemaCache[schemaKey]
        ?: throw SchemaFetchingException("Could not fetch schema fot key $schemaKey")

    private fun retrieveSchema(key: SchemaKey) = try {
        schemaRepository.get(key)
    } catch (e: SchemaFetchingException) {
        null
    }
}
