package br.com.guiabolso.hyperloop.validation.v2.parser.tree

import br.com.guiabolso.hyperloop.exceptions.InvalidInputException
import br.com.guiabolso.hyperloop.validation.PrimitiveTypes
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive


data class ScalarNode(
    val type: PrimitiveTypes,
    val encrypted: Boolean,
    val path: String,
    val required: Boolean,
    val nullable: Boolean
) {

    fun validate(element: JsonElement) {
        if (element is JsonNull && !nullable) {
            InvalidInputException("Element in path $path cannot be null.")
        } else if (element is JsonPrimitive) {
            type.verifyType(element)
        } else throw IllegalStateException("Element is not null or primitive. This probably is a parser bug.")
    }

    override fun toString() = "$path -> required: $required nullable: $nullable encrypted: $encrypted"
}