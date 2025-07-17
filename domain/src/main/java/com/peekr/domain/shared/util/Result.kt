package com.peekr.domain.shared.util

typealias BaseError = Error

/** 결과 래퍼 클래스 */
sealed interface Result<out T, out E : BaseError> {
    /**
     * 성공 시
     *
     * @property data 성공 후 반환할 데이터
     */
    data class Success<out T>(val data: T) : Result<T, Nothing>

    /**
     * 실패 시
     *
     * @property error 에러 타입
     * @property message 에러 메시지
     * @property detail 에러 부가 설명
     */
    data class Error<out E : BaseError>(
        val error: E,
        val message: String? = null,
        val detail: String? = null,
    ) : Result<Nothing, E>
}
