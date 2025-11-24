package com.peekr.core.data.source.network.error

import com.squareup.moshi.JsonClass

/**
 * 통합 에러 응답 바디
 *
 * ```
 * // Example
 * {
 *   "code": "A001",
 *   "message": "Login failed",
 *   "status": 400
 * }
 * ```
 *
 * @property code 에러 코드 (서버에서 제공되는 에러 코드)
 * @property message 에러 메시지
 * @property status 에러 코드 (보통 HTTP 상태 코드 형태)
 */
@JsonClass(generateAdapter = true)
data class CommonErrorResponse(
    val code: String,
    val message: String,
    val status: Int,
)
