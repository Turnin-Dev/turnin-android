package com.peekr.core.domain.common

/** 서버와 통일된 에러 코드 */
sealed interface ErrorCode {
    data object Unexpected : ErrorCode

    enum class Auth : ErrorCode {
        A002,
    }
}
