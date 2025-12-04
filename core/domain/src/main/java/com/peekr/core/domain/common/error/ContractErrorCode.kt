package com.peekr.core.domain.common.error

/** 외부 시스템(Ex. 서버)과 계약된 에러 코드 */
sealed interface ContractErrorCode {
    data object Unexpected : ContractErrorCode

    enum class Auth : ContractErrorCode {
        A002,
    }
}
