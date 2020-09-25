package br.com.guiabolso.hyperloop.cryptography

interface CryptographyEngine {

    fun decrypt(data: EncryptedData): String

    fun encrypt(plainText: String): EncryptedData
}
