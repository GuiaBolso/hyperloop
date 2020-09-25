package br.com.guiabolso.hyperloop.schemas

import br.com.guiabolso.hyperloop.schemas.exceptions.SchemaFetchingException

interface SchemaRepository<out R> {

    @Throws(SchemaFetchingException::class)
    fun get(schemaKey: SchemaKey): R
}
