package br.com.guiabolso.hyperloop

import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.hyperloop.cryptography.cypher.MessageCypher
import br.com.guiabolso.hyperloop.cryptography.cypher.NoOpMessageCypher
import br.com.guiabolso.hyperloop.environment.getEnv
import br.com.guiabolso.hyperloop.exceptions.SendMessageException
import br.com.guiabolso.hyperloop.schemas.aws.S3SchemaRepository
import br.com.guiabolso.hyperloop.transport.MessageResult
import br.com.guiabolso.hyperloop.transport.Transport
import br.com.guiabolso.hyperloop.util.Clock
import br.com.guiabolso.hyperloop.util.DefaultClock
import br.com.guiabolso.hyperloop.util.isoFormat
import br.com.guiabolso.hyperloop.validation.Validator
import br.com.guiabolso.hyperloop.validation.VersionedEventValidator
import br.com.guiabolso.hyperloop.validation.exceptions.ValidationException
import com.amazonaws.regions.Regions
import com.github.salomonbrys.kotson.set
import com.google.gson.GsonBuilder

class Hyperloop
@JvmOverloads
constructor(
    private val transport: Transport,
    private val validator: Validator = defaultValidator(),
    private val messageCypher: MessageCypher = NoOpMessageCypher,
    private val clock: Clock = DefaultClock
) {

    private val gson = GsonBuilder().serializeNulls().create()

    fun offer(event: RequestEvent): MessageResult {
        event.metadata["receivedAt"] = clock.now().isoFormat()
        val validationResult = validator.validate(event)

        if (!validationResult.validationSuccess) {
            throw ValidationException("Error validating event against schema", validationResult.validationErrors)
        }

        val encryptedData = messageCypher.cypher(gson.toJson(event))

        val messageResult = transport.sendMessage(encryptedData)

        if (messageResult.messageMD5 != encryptedData.md5()) {
            throw SendMessageException("Transport could not deliver message correctly! MD5 from event differs from MD5 of transport.")
        }
        return messageResult
    }

    companion object {
        @JvmStatic
        private fun defaultValidator(): VersionedEventValidator {
            val bucket = getEnv("HYPERLOOP_BUCKET", "hyperloop-schemas")
            val region = Regions.fromName(getEnv("HYPERLOOP_REGION", "sa-east-1"))
            val s3SchemaRepository = S3SchemaRepository(bucket, region)
            return VersionedEventValidator(s3SchemaRepository)
        }
    }
}