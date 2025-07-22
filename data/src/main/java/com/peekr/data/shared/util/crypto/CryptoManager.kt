package com.peekr.data.shared.util.crypto

import java.util.Base64
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * 암호화/복호화를 처리한다.
 *
 * [encryptString], [decryptString]는 반드시 예외 처리 필요
 *
 * @see [Crypto]
 */
class CryptoManager(private val ioDispatcher: CoroutineDispatcher) {
    /**
     * 평문 텍스트를 암호화한다.
     *
     * @param plainText 평문 텍스트
     * @return 암호화된 텍스트
     * @throws EncryptException 암호화 실패 시
     */
    suspend fun encryptString(plainText: String): String = withContext(ioDispatcher) {
        try {
            val bytes = plainText.toByteArray()
            val encryptedBytes = Crypto.encrypt(bytes)
            Base64.getEncoder().encodeToString(encryptedBytes)
        } catch (e: Exception) {
            Timber.e(e, "Encrypt Exception")
            throw EncryptException(e)
        }
    }

    /**
     * 암호화된 텍스트를 복호화한다.
     *
     * @param encryptedText 암호화된 텍스트
     * @return 복호화된 텍스트
     * @throws DecryptException 복호화 실패 시
     */
    suspend fun decryptString(encryptedText: String): String = withContext(ioDispatcher) {
        try {
            val encryptedBytesDecoded = Base64.getDecoder().decode(encryptedText)
            val decryptedBytes = Crypto.decrypt(encryptedBytesDecoded)
            decryptedBytes.decodeToString()
        } catch (e: Exception) {
            Timber.e(e, "Decrypt Exception")
            throw DecryptException(e)
        }
    }
}
