package com.peekr.core.data.source.network.datasource

import com.peekr.core.data.source.network.dto.discover.response.DiscoverContextCursorPageResponse
import com.peekr.core.data.source.network.util.NetworkResult

/**
 * 탐색 네트워크 데이터 소스
 */
interface DiscoverNetworkDataSource {
    /**
     * 탐색 컨텍스트 페이지네이션 조회
     *
     * 탐색 컨텍스트: 사용자 정보 일부 + 키워드 정보 일부
     *
     * @param userId 사용자 ID
     * @param cursor 커서 값
     * @param size 페이지 크기
     */
    suspend fun getDiscoverContexts(
        userId: Long,
        cursor: Long?,
        size: Int,
    ): NetworkResult<DiscoverContextCursorPageResponse>
}
