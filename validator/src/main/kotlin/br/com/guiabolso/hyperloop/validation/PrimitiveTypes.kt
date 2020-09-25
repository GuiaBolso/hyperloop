package br.com.guiabolso.hyperloop.validation

import br.com.guiabolso.hyperloop.exceptions.InvalidInputException
import com.google.gson.JsonPrimitive
import java.text.ParseException
import java.time.format.DateTimeFormatter

enum class PrimitiveTypes {
    STRING {
        override fun verifyType(element: JsonPrimitive, nodeKey: String) {
            if (!element.isString) {
                throw InvalidInputException("Input $element is not a string on nodekey $nodeKey")
            }
        }
    },
    LONG {
        override fun verifyType(element: JsonPrimitive, nodeKey: String) {
            try {
                element.asLong
            } catch (exception: Exception) {
                throw InvalidInputException("Input $element is not a long on nodekey $nodeKey")
            }
        }
    },
    INT {
        override fun verifyType(element: JsonPrimitive, nodeKey: String) {
            try {
                element.asInt
            } catch (exception: Exception) {
                throw InvalidInputException("Input $element is not an int on nodekey $nodeKey")
            }
        }
    },
    FLOAT {
        override fun verifyType(element: JsonPrimitive, nodeKey: String) {
            try {
                element.asFloat
            } catch (exception: Exception) {
                throw InvalidInputException("Input $element is not a float on nodekey $nodeKey")
            }
        }
    },
    DOUBLE {
        override fun verifyType(element: JsonPrimitive, nodeKey: String) {
            try {
                element.asDouble
            } catch (exception: Exception) {
                throw InvalidInputException("Input $element is not a double on nodekey $nodeKey")
            }
        }
    },
    BOOLEAN {
        override fun verifyType(element: JsonPrimitive, nodeKey: String) {
            if (!element.isBoolean) {
                throw InvalidInputException("Input $element is not a boolean on nodekey $nodeKey")
            }
        }
    },
    DATETIME {
        override fun verifyType(element: JsonPrimitive, nodeKey: String) {
            try {
                DateTimeFormatter.ISO_INSTANT.parse(element.asString)
            } catch (e: ParseException) {
                throw InvalidInputException("Date Element '${element.asString}' is not a INSTANT DATE on nodekey $nodeKey")
            }
        }
    };

    companion object {
        fun valueOfOrNull(value: String): PrimitiveTypes? {
            return try {
                PrimitiveTypes.valueOf(value)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }

    abstract fun verifyType(element: JsonPrimitive, nodeKey: String)
}
