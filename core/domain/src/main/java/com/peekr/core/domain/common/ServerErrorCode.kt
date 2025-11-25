package com.peekr.core.domain.common

/** 서버와 통일된 에러 코드 */
sealed interface ServerErrorCode {
    data object Unexpected : ServerErrorCode

    enum class Auth : ServerErrorCode {
        A002,
    }
}
