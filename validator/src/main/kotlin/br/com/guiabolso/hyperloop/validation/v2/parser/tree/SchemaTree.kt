package br.com.guiabolso.hyperloop.validation.v2.parser.tree

class SchemaTree(
    private val treeMap: Map<String, ScalarNode>
) : Iterable<Map.Entry<String, ScalarNode>> {

    override fun iterator(): Iterator<Map.Entry<String, ScalarNode>> {
        return treeMap.iterator()
    }

    operator fun contains(jsonPath: String) = cleanJsonPath(jsonPath) in treeMap

    operator fun get(jsonPath: String) = treeMap[jsonPath]

    private fun cleanJsonPath(jsonPath: String) = jsonPath.replace(Regex("\\[\\d+]"), "")
}
