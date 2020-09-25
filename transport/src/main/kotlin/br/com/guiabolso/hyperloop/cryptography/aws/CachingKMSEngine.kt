package br.com.guiabolso.hyperloop.cryptography.aws

import br.com.guiabolso.hyperloop.cryptography.CryptographyEngine
import br.com.guiabolso.hyperloop.cryptography.EncryptedData
import com.amazonaws.encryptionsdk.AwsCrypto
import com.amazonaws.encryptionsdk.CommitmentPolicy.ForbidEncryptAllowDecrypt
import com.amazonaws.encryptionsdk.caching.CachingCryptoMaterialsManager
import com.amazonaws.encryptionsdk.caching.LocalCryptoMaterialsCache
import com.amazonaws.encryptionsdk.kms.KmsMasterKeyProvider
import java.util.concurrent.TimeUnit

class CachingKMSEngine(
    kmsConfiguration: KMSConfiguration
) : CryptographyEngine {

    private val crypto = AwsCrypto.builder()
        .withCommitmentPolicy(ForbidEncryptAllowDecrypt)
        .build()

    private val cache = CachingCryptoMaterialsManager.newBuilder()
        .withMasterKeyProvider(
            KmsMasterKeyProvider.builder().buildDiscovery()
                .getMasterKey(kmsConfiguration.encryptionKey)
        )
        .withCache(LocalCryptoMaterialsCache(kmsConfiguration.cacheMaxSize))
        .withMaxAge(kmsConfiguration.keyMaxAgeMinutes, TimeUnit.MINUTES)
        .withMessageUseLimit(kmsConfiguration.keyUsageLimit)
        .build()

    override fun encrypt(plainText: String): EncryptedData {
        if (plainText.isBlank()) return EncryptedData(byteArrayOf())
        val encryptedData = crypto.encryptData(cache, plainText.toByteArray()).getResult()
        return EncryptedData(encryptedData)
    }

    override fun decrypt(data: EncryptedData): String {
        if (data.data.isEmpty()) return ""
        val decryptedData = crypto.decryptData(cache, data.data).getResult()
        return String(decryptedData)
    }
}
