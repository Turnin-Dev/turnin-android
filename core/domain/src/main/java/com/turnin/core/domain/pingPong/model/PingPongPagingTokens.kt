package com.turnin.core.domain.pingPong.model

/**
 * 핑퐁 목록 페이징 설정 값
 *
 * 서버의 페이지 크기 허용 범위는 1 ~ 25이므로 [PAGE_SIZE]는 이 범위를 벗어나면 안 된다.
 */
object PingPongPagingTokens {
    const val PAGE_SIZE = 15
    const val PREFETCH_DISTANCE = 5
}
