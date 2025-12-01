package com.peekr.core.domain.common

sealed interface CommonErrorType : BaseError {
    /** 로컬에서 발생한 에러 타입 */
    enum class Local : CommonErrorType {
        /** 데이터를 쓰는 과정에서 발생한 에러 */
        WritingDataFailed,
    }

    /** 네트워크에서 발생한 에러 타입 */
    enum class Network : CommonErrorType {
        /** 허가되지 않은 인증 */
        Unauthorized, // 401

        /** 클라이언트에서 발생한 에러 */
        ClientError,

        /** 서버 상에서 발생한 에러 */
        ServerError,

        /** 서버 및 네트워크 연결 에러 */
        ConnectionFailed,

        /** 지원하지 않는 파일 유형 */
        InvalidFileType,

        /** 파일 업로드 실패 에러 */
        UploadFileFailed,
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
