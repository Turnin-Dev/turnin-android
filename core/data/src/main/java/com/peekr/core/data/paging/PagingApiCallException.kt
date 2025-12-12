package com.peekr.core.data.paging

import com.peekr.core.data.source.network.error.NetworkErrorType

/**
 * 페이징 도중 발생하는 API 호출 예외
 *
 * @property error 네트워크 에러 타입
 * @property code 네트워크 에러 코드
 * @property status HTTP 상태 코드
 * @property message 에러 메시지
 */
data class PagingApiCallException(
    val error: NetworkErrorType,
    val code: String? = null,
    val status: Int? = null,
    override val message: String?,
) : Exception(message)
