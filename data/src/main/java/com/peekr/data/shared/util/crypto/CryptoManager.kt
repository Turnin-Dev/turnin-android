package com.peekr.data.shared.util.crypto

import java.util.Base64
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

@Singleton
class CryptoManager @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
) {
    suspend fun encryptString(plainText: String): String = withContext(ioDispatcher) {
        val bytes = plainText.toByteArray()
        val encryptedBytes = Crypto.encrypt(bytes)
        Base64.getEncoder().encodeToString(encryptedBytes)
    }

    suspend fun decryptString(encryptedText: String): String = withContext(ioDispatcher) {
        val encryptedBytesDecoded = Base64.getDecoder().decode(encryptedText)
        val decryptedBytes = Crypto.decrypt(encryptedBytesDecoded)
        decryptedBytes.decodeToString()
    }
}
