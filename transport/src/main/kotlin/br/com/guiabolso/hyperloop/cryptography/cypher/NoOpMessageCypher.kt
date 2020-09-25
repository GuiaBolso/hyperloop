package br.com.guiabolso.hyperloop.cryptography.cypher

object NoOpMessageCypher : MessageCypher {
    override fun cypher(plainMessage: String): String {
        return plainMessage
    }
}
