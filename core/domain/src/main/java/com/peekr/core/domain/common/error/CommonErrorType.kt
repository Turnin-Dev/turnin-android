package com.peekr.core.domain.common.error

import com.peekr.core.domain.common.BaseError

sealed interface CommonErrorType : BaseError {
    /** 로컬에서 발생한 에러 타입 */
    enum class Local : CommonErrorType {
        /** 데이터를 쓰는 과정에서 발생한 에러 */
        WritingDataFailed,

        /** 있어야 할 값이 비어있는 경우 */
        Empty,
    }

    /** 네트워크에서 발생한 에러 타입 */
    sealed interface Network : CommonErrorType {
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

        /** 지원하지 않는 파일 유형 */
        data object InvalidFileType : Network

        /** 파일 업로드 실패 에러 */
        data object UploadFileFailed : Network
    }

    /** 예외 타입 */
    enum class Exception : CommonErrorType {
        Json,
        TimeOut,
        IO,
    }

    /** 알 수 없는 에러로 자세한 사항은 [cause] 파라미터에 [Throwable]형태로 담는다. */
    data class Unexpected(val cause: Throwable?) : CommonErrorType
}
