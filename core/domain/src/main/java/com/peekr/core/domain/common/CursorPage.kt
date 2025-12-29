package com.peekr.core.domain.common

/**
 * 커서 기반 페이지네이션에서 사용한다.
 */
data class CursorPage<T>(
    val items: List<T>,
    val nextCursor: Long?,
)
