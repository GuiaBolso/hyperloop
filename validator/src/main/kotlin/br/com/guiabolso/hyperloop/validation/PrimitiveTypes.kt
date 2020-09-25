package br.com.guiabolso.hyperloop.validation

import br.com.guiabolso.hyperloop.exceptions.InvalidInputException
import com.google.gson.JsonPrimitive
import java.text.ParseException
import java.time.format.DateTimeFormatter

enum class PrimitiveTypes {
    STRING {
        override fun verifyType(element: JsonPrimitive) {
            if (!element.isString) {
                throw InvalidInputException("Input $element is not a string")
            }
        }
    },
    LONG {
        override fun verifyType(element: JsonPrimitive) {
            try {
                element.asLong
            } catch (exception: Exception) {
                throw InvalidInputException("Input $element is not a long")
            }
        }
    },
    INT {
        override fun verifyType(element: JsonPrimitive) {
            try {
                element.asInt
            } catch (exception: Exception) {
                throw InvalidInputException("Input $element is not an int")
            }
        }
    },
    FLOAT {
        override fun verifyType(element: JsonPrimitive) {
            try {
                element.asFloat
            } catch (exception: Exception) {
                throw InvalidInputException("Input $element is not a float")
            }
        }
    },
    DOUBLE {
        override fun verifyType(element: JsonPrimitive) {
            try {
                element.asDouble
            } catch (exception: Exception) {
                throw InvalidInputException("Input $element is not a double")
            }
        }
    },
    BOOLEAN {
        override fun verifyType(element: JsonPrimitive) {
            if (!element.isBoolean) {
                throw InvalidInputException("Input $element is not a boolean")
            }
        }
    },
    DATETIME {
        override fun verifyType(element: JsonPrimitive) {
            try {
                DateTimeFormatter.ISO_INSTANT.parse(element.asString)
            } catch (e: ParseException) {
                throw InvalidInputException("Date Element '${element.asString}' is not a INSTANT DATE")
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

    abstract fun verifyType(element: JsonPrimitive)
}
