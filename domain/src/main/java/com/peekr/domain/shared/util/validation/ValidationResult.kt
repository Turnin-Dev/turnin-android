package com.peekr.domain.shared.util.validation

/** 유효성 검사 결과 클래스 */
sealed interface ValidationResult {
    /** 기본 값 */
    data object Idle : ValidationResult

    /** 로딩 시 (유효성 검사에서 시간이 걸릴 경우) */
    data object Loading : ValidationResult

    /** 유효성 검사 성공 시 */
    data object Success : ValidationResult

    /**
     * 유효성 검사 실패 시
     *
     * @param error 유효성 검사 에러 [ValidationError]
     */
    data class Error(val error: ValidationError) : ValidationResult
}
