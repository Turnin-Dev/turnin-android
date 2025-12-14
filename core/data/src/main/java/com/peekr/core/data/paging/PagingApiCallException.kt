package com.peekr.core.data.paging

import com.peekr.core.domain.common.error.CommonErrorType

/**
 * 페이징 도중 발생하는 API 호출 예외
 *
 * @property error 공통 에러 타입
 * @property message 에러 메시지
 */
data class PagingApiCallException(
    val error: CommonErrorType,
    override val message: String?,
) : Exception(message)
