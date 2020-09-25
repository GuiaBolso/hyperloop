package br.com.guiabolso.hyperloop.validation.exceptions

class ValidationException(
    message: String,
    private val errors: Set<Throwable>
) : RuntimeException(message) {

    override val message: String?
        get() {
            val errorMessage = errors.joinToString("\n\t") { t: Throwable ->
                t.message ?: ""
            }
            return "${super.message}\nerrors:\n\t$errorMessage"
        }
}
