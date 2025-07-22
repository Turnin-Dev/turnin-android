package com.peekr.data.shared.util.crypto

sealed class CryptoException(message: String, cause: Throwable? = null) : Exception(message, cause)

class EncryptException(cause: Throwable? = null) :
    CryptoException("암호화 실패", cause)

class DecryptException(cause: Throwable? = null) :
    CryptoException("복호화 실패", cause)
