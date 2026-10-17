package com.turnin.shared.core.error

import com.turnin.shared.core.result.BaseError

/**
 * 네트워크 에러 타입
 *
 * `:core:domain`의 `CommonErrorType` 중 네트워크·예외 항목과 같은 구성이다.
 * (Android에서는 `CommonErrorType`으로 1:1 변환해 기존 에러 문구를 사용한다.)
 */
sealed interface NetworkErrorType : BaseError {
    /** 서버 응답·연결 에러 */
    sealed interface Network : NetworkErrorType {
        data object BadRequest : Network

        data object Unauthorized : Network

        data object Forbidden : Network

        data object NotFound : Network

        data object Conflict : Network

        data object RequestTimeout : Network

        data object InternalServerError : Network

        data object BadGateway : Network

        data object ServiceUnavailable : Network

        data object GatewayTimeout : Network

        data class ClientError(
            val status: Int,
        ) : Network

        data class ServerError(
            val status: Int,
        ) : Network

        /** 서버 및 네트워크 연결 에러 */
        data object ConnectionFailed : Network
    }

    /** 예외 타입 */
    enum class Exception : NetworkErrorType {
        Json,
        TimeOut,
        IO,
    }

    /** 알 수 없는 에러로 자세한 사항은 [cause] 파라미터에 [Throwable]형태로 담는다. */
    data class Unexpected(val cause: Throwable?) : NetworkErrorType
}
