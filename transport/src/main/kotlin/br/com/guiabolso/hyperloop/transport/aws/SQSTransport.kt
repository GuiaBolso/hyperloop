package br.com.guiabolso.hyperloop.transport.aws

import br.com.guiabolso.hyperloop.transport.MessageResult
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

    override fun sendMessage(message: String): MessageResult {
        val sendMessageRequest = SendMessageRequest()
            .withQueueUrl(queueURL)
            .withMessageBody(message)

        val messageResult = sqs.sendMessage(sendMessageRequest)

        return MessageResult(messageResult.messageId, messageResult.mD5OfMessageBody)
    }
}
