package com.peekr.core.data.source.local.error

import java.io.IOException

/** DataStoreManager 예외 */
sealed class DataStoreException(message: String, cause: Throwable?) : IOException(message, cause)

/** DataStoreManager 에서 데이터를 수정할 때 발생할 수 있는 예외 (공통적으로 사용) */
class WritingDataException(message: String, cause: Throwable?) : DataStoreException(message, cause)

/** DataStoreManager 에서 복호화 과정에서 발생할 수 있는 예외 (암호화 관련 예외) */
class DecryptException(message: String, cause: Throwable?) : DataStoreException(message, cause)
