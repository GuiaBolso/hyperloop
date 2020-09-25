package br.com.guiabolso.hyperloop.validation

data class ValidationResult(
    var validationSuccess: Boolean,
    val validationErrors: MutableSet<Throwable>,
    val encryptedFields: MutableSet<String>
)
