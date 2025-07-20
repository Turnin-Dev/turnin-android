package com.peekr.domain.shared.dataStore

import java.io.IOException

/** DataStoreManager 예외 */
sealed class DataStoreException(message: String) : IOException(message)

/** DataStoreManager 에서 데이터를 수정할 때 발생할 수 있는 예외 (공통적으로 사용) */
class WritingDataException(message: String) : DataStoreException(message)
