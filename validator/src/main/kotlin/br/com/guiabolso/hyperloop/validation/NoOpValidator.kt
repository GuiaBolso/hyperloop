package br.com.guiabolso.hyperloop.validation

import br.com.guiabolso.events.model.RequestEvent

object NoOpValidator : Validator {
    override fun validate(event: RequestEvent) = ValidationResult(
        validationSuccess = true,
        validationErrors = mutableSetOf(),
        encryptedFields = mutableSetOf()
    )
}
