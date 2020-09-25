package br.com.guiabolso.hyperloop.transport.aws

import br.com.guiabolso.hyperloop.transport.MessageResult
import br.com.guiabolso.hyperloop.transport.Transport
import com.amazonaws.regions.Regions
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder
import com.amazonaws.services.sqs.buffered.AmazonSQSBufferedAsyncClient
import com.amazonaws.services.sqs.buffered.QueueBufferConfig
import com.amazonaws.services.sqs.model.SendMessageRequest

class BufferedSQSTransport(
    private val queueURL: String,
    region: Regions,
    private val maxBatchOpenMs: Long = 1000
) : Transport, AutoCloseable {

    private val sqs = AmazonSQSBufferedAsyncClient(
        AmazonSQSAsyncClientBuilder
            .standard()
            .withRegion(region)
            .build(),
        QueueBufferConfig().apply {
            withAdapativePrefetching(false)
            withFlushOnShutdown(true)
            withMaxBatchOpenMs(this@BufferedSQSTransport.maxBatchOpenMs)
        }
    )

    override fun sendMessage(message: String): MessageResult {
        val sendMessageRequest = SendMessageRequest()
            .withQueueUrl(queueURL)
            .withMessageBody(message)

        val messageResult = sqs.sendMessage(sendMessageRequest)

        return MessageResult(messageResult.messageId, messageResult.mD5OfMessageBody)
    }

    override fun close() {
        sqs.shutdown()
    }
}
