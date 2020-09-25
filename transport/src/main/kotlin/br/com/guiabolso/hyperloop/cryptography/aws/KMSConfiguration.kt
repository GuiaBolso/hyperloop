package br.com.guiabolso.hyperloop.cryptography.aws

data class KMSConfiguration(
    val encryptionKey: String,
    val cacheMaxSize: Int,
    val keyMaxAgeMinutes: Long,
    val keyUsageLimit: Long
)
