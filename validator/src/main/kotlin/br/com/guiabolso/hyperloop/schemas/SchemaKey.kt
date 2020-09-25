package br.com.guiabolso.hyperloop.schemas

data class SchemaKey(
    val name: String,
    val version: Int
) {

    override fun toString(): String {
        return "${name}_V$version.yaml".replace(':', '_')
    }
}
