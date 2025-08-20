package com.peekr.domain.shared.util

interface ValidationError

/**
 * 유효성 검사 결과 클래스
 *
 * [E]는 [ValidationError]의 하위타입이다.
 */
sealed interface ValidationResult<out E : ValidationError> {
    /** 로딩 시 (유효성 검사에서 시간이 걸릴 경우) */
    data object Loading : ValidationResult<Nothing>

    /** 유효성 검사 성공 시 */
    data object Success : ValidationResult<Nothing>

    /**
     * 유효성 검사 실패 시
     *
     * @param error 유효성 검사 에러
     */
    data class Error<out E : ValidationError>(val error: E) : ValidationResult<E>
}
