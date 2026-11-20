package com.turnin.core.domain.common.error

import com.turnin.core.domain.common.BaseError

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
        /** 잘못된 요청인 경우 (HTTP 400) */
        data object BadRequest : Network

        /** 인증되지 않은 경우 (HTTP 401) */
        data object Unauthorized : Network

        /** 접근 권한이 없는 경우 (HTTP 403) */
        data object Forbidden : Network

        /** 리소스를 찾을 수 없는 경우 (HTTP 404) */
        data object NotFound : Network

        /** 리소스가 충돌(중복)하는 경우 (HTTP 409) */
        data object Conflict : Network

        /** 요청 시간이 초과된 경우 (HTTP 408) */
        data object RequestTimeout : Network

        /** 요청 횟수 제한을 초과한 경우 (HTTP 429) */
        data object TooManyRequests : Network

        /** 서버 내부 오류인 경우 (HTTP 500) */
        data object InternalServerError : Network

        /** 게이트웨이가 잘못된 응답을 받은 경우 (HTTP 502) */
        data object BadGateway : Network

        /** 서비스를 일시적으로 사용할 수 없는 경우 (HTTP 503) */
        data object ServiceUnavailable : Network

        /** 게이트웨이 응답 시간이 초과된 경우 (HTTP 504) */
        data object GatewayTimeout : Network

        /** 따로 정의하지 않은 클라이언트 오류인 경우 (그 외 HTTP 4xx) */
        data class ClientError(
            val status: Int,
        ) : Network

        /** 따로 정의하지 않은 서버 오류인 경우 (그 외 HTTP 5xx) */
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

        /** 카카오 계정 삭제 에러 */
        data object KakaoDeleteAccountError : SocialAuth

        /** 카카오 로그인 필요 */
        data object KakaoLoginRequired : SocialAuth

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
