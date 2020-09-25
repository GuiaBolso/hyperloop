package br.com.guiabolso.hyperloop.transport

interface Transport {

    fun sendMessage(message: String): MessageResult
}
