package com.turnin.core.data.crypto

/**
 * 암호화/복호화 시 발생하는 예외
 *
 * 주로 [Crypto], [CryptoManager]에서 발생한다.
 */
sealed class CryptoException(message: String, cause: Throwable? = null) : Exception(message, cause)

/** 암호화 실패 관련 예외 */
class EncryptException(cause: Throwable? = null) :
    CryptoException("암호화 실패", cause)

/** 복호화 실패 관련 예외 */
class DecryptException(cause: Throwable? = null) :
    CryptoException("복호화 실패", cause)
