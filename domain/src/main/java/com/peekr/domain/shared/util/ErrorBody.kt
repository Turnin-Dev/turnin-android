package com.peekr.domain.shared.util

/**
 * 공통 에러 바디
 *
 * @property code 에러 코드
 * @property message 에러 메시지
 * @property status 에러 코드 (보통 HTTP 상태 코드 형태)
 */
data class ErrorBody(
    val code: String,
    val message: String,
    val status: Int,
)
