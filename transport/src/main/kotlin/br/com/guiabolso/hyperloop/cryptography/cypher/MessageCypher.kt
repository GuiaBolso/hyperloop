package br.com.guiabolso.hyperloop.cryptography.cypher

interface MessageCypher {
    fun cypher(plainMessage: String): String
}
