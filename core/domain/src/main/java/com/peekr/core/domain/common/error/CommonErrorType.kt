package com.peekr.core.domain.common.error

import com.peekr.core.domain.common.BaseError

sealed interface CommonErrorType : BaseError {
    /** 로컬에서 발생한 에러 타입 */
    enum class Local : CommonErrorType {
        /** 데이터를 쓰는 과정에서 발생한 에러 */
        WritingDataFailed,

        /** 있어야 할 값이 비어있는 경우 */
        Empty,

        /** 로컬에 저장된 사용자 ID를 찾을 수 없는 경우 */
        UserIdNotFound,
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

    sealed interface SocialAuth : CommonErrorType {
        /** ID 토큰 파싱 에러 */
        data object IdTokenParsing : SocialAuth

        /** 인증 로직 취소 에러 */
        data object Cancellation : SocialAuth

        /** 유효하지 않은 토큰 타입 에러 */
        data object TokenTypeInvalid : SocialAuth

        /** 사용자를 찾을 수 없는 에러 */
        data object UserNotFound : SocialAuth

        /** 계정 삭제 실패 에러 */
        data object DeleteAccountFailed : SocialAuth

        /** 카카오 로그인 에러 */
        data object KakaoSignInError : SocialAuth

        /** 카카오 로그아웃 에러 */
        data object KakaoSignOutError : SocialAuth

        /**
         * 카카오 계정 삭제 에러
         */
        data object KakaoDeleteAccountError : SocialAuth

        /** 토큰 저장 실패 에러 */
        data object SaveTokenFailed : SocialAuth

        /** 로그인 실패 에러 */
        data object SocialAuthFailed : SocialAuth

        /** 소셜 로그인 공급자를 찾을 수 없는 경우 */
        data object LoginProviderNotFound : SocialAuth

        /** 알 수 없는 에러로 자세한 사항은 [cause] 파라미터에 [Throwable]형태로 담는다. */
        data class Unexpected(val cause: Throwable?) : SocialAuth
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
