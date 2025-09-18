package com.peekr.domain.common.util

/**
 * 유효성 검사 결과 클래스
 */
sealed interface ValidationResult {
    /** 로딩 시 (유효성 검사에서 시간이 걸릴 경우) */
    data object Loading : ValidationResult

    /** 유효성 검사 성공 시 */
    data object Success : ValidationResult

    /**
     * 유효성 검사 실패 시
     *
     * @param error 유효성 검사 에러
     */
    data class Error(val error: ValidationError) : ValidationResult
}
