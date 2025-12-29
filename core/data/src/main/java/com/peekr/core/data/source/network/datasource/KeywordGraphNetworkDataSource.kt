package com.peekr.core.data.source.network.datasource

import com.peekr.core.data.source.network.dto.keywordGraph.response.NodeContextCursorPageResponse
import com.peekr.core.data.source.network.util.NetworkResult

interface KeywordGraphNetworkDataSource {
    /**
     * 노드 컨텍스트 페이지네이션 조회
     *
     * @param userId 조회할 사용자 ID
     * @param cursor 커서
     * @param size 페이지 사이즈
     */
    suspend fun getNodeContexts(
        userId: Long,
        cursor: Long?,
        size: Int,
    ): NetworkResult<NodeContextCursorPageResponse>
}
