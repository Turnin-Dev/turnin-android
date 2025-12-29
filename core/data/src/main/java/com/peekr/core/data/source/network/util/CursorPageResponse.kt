package com.peekr.core.data.source.network.util

/**
 * 커서 기반 페이지네이션 응답 바디를 구현하는 경우 해당 인터페이스를 구현해서 사용한다.
 *
 * @property T 항목 타입
 * @property C 커서 타입
 * @property items 항목 목록
 * @property nextCursor 다음 커서 값
 */
interface CursorPageResponse<T, C> {
    val items: List<T>
    val nextCursor: C?
}
