package br.com.guiabolso.hyperloop.schemas

import br.com.guiabolso.hyperloop.model.SchemaData
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue

class SchemaDataRepository(
    private val schemaRepository: SchemaRepository<String>
) : SchemaRepository<SchemaData> {

    override fun get(schemaKey: SchemaKey): SchemaData {
        val schemaStr = schemaRepository.get(schemaKey)
        return mapper.readValue(schemaStr)
    }

    companion object {
        private val mapper = ObjectMapper(YAMLFactory())
            .registerModule(KotlinModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
    }
}
