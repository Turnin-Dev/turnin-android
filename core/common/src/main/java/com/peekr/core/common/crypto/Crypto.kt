package com.peekr.core.common.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.peekr.core.common.logger.AppLogger
import java.security.KeyStore
import javax.crypto.AEADBadTagException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * 암호화 유틸
 *
 * 참고 링크(`https://github.com/philipplackner/EncryptedDataStore`)
 */
object Crypto {
    private val tag = this::class.java.simpleName
    private const val KEY_ALIAS = "secret"
    private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
    private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM
    private const val PADDING = KeyProperties.ENCRYPTION_PADDING_NONE
    private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
    private const val IV_SIZE = 12
    private const val TAG_SIZE = 128
    private const val KEY_SIZE = 256

    private val keyStore = KeyStore
        .getInstance("AndroidKeyStore")
        .apply {
            load(null)
        }

    private fun getKey(): SecretKey {
        val existingKey = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createKey()
    }

    private fun createKey(): SecretKey = KeyGenerator
        .getInstance(ALGORITHM)
        .apply {
            init(
                KeyGenParameterSpec
                    .Builder(
                        KEY_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
                    ).setBlockModes(BLOCK_MODE)
                    .setEncryptionPaddings(PADDING)
                    .setKeySize(KEY_SIZE)
                    .setRandomizedEncryptionRequired(true)
                    .setUserAuthenticationRequired(false)
                    .build(),
            )
        }.generateKey()

    private fun getCipher(): Cipher = Cipher.getInstance(TRANSFORMATION)

    /**
     * 암호화
     *
     * @param bytes 평문 데이터의 ByteArray
     * @throws EncryptException 암호화 실패 시 예외 발생
     */
    fun encrypt(bytes: ByteArray): ByteArray = try {
        val cipher = getCipher()
        cipher.init(Cipher.ENCRYPT_MODE, getKey())
        val iv = cipher.iv
        val encrypted = cipher.doFinal(bytes)
        iv + encrypted
    } catch (e: Exception) {
        throw EncryptException(e)
    }

    /**
     * 복호화
     *
     * @param bytes 암호화된 데이터의 ByteArray
     * @throws DecryptException 복호화 실패 시 예외 발생
     */
    fun decrypt(bytes: ByteArray): ByteArray = try {
        val iv = bytes.copyOfRange(0, IV_SIZE)
        val data = bytes.copyOfRange(IV_SIZE, bytes.size)
        val cipher = getCipher()
        cipher.init(Cipher.DECRYPT_MODE, getKey(), GCMParameterSpec(TAG_SIZE, iv))
        cipher.doFinal(data)
    } catch (e: AEADBadTagException) {
        AppLogger.e(tag, e, "데이터 손상 및 암호문 위변조 시도")
        throw DecryptException(e)
    } catch (e: Exception) {
        throw DecryptException(e)
    }
}
