package com.peekr.core.domain.common

/**
 * 에러 베이스 타입
 *
 * 다른 도메인에서 이를 확장하여 커스텀 에러를 정의할 때 도메인 계층에서 정의되어야 한다.
 */
interface BaseError

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
     * @property message 디버깅 & 로그용 메시지
     * @property code 계약된 에러 코드
     */
    data class Error<out E : BaseError>(
        val error: E,
        val message: String? = null,
        val code: ContractErrorCode = ContractErrorCode.Unexpected,
    ) : Result<Nothing, E>

    data object Loading : Result<Nothing, Nothing>
}
