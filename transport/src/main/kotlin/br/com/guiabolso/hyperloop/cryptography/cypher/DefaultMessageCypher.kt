package br.com.guiabolso.hyperloop.cryptography.cypher

import br.com.guiabolso.hyperloop.b64
import br.com.guiabolso.hyperloop.cryptography.CryptographyEngine

class DefaultMessageCypher(
    private val cryptographyEngine: CryptographyEngine
) : MessageCypher {

    override fun cypher(plainMessage: String): String {
        val encryptedData = cryptographyEngine.encrypt(plainMessage)
        return encryptedData.data.b64()
    }
}
