package br.com.guiabolso.hyperloop.exceptions

import java.util.Objects

class InvalidInputException(message: String) : RuntimeException(message) {

    override fun equals(other: Any?): Boolean {
        if (other !is InvalidInputException) return false
        if (this.message != other.message) return false
        return true
    }

    override fun hashCode(): Int {
        return Objects.hashCode(this.message)
    }
}
