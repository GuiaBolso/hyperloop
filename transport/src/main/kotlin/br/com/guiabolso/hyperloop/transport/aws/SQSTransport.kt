package br.com.guiabolso.hyperloop.transport.aws

import br.com.guiabolso.hyperloop.exceptions.SendMessageException
import br.com.guiabolso.hyperloop.md5
import br.com.guiabolso.hyperloop.transport.Transport
import com.amazonaws.regions.Regions
import com.amazonaws.services.sqs.AmazonSQSClientBuilder
import com.amazonaws.services.sqs.model.SendMessageRequest

class SQSTransport(
    private val queueURL: String,
    region: Regions
) : Transport {

    private val sqs = AmazonSQSClientBuilder
        .standard()
        .withRegion(region)
        .build()

    override fun sendMessage(message: String) {
        val sendMessageRequest = SendMessageRequest()
            .withQueueUrl(queueURL)
            .withMessageBody(message)

        val messageResult = sqs.sendMessage(sendMessageRequest)
        if (messageResult.mD5OfMessageBody != message.md5()) {
            throw SendMessageException("Transport could not deliver message correctly! MD5 from event differs from MD5 of transport.")
        }
    }
}
