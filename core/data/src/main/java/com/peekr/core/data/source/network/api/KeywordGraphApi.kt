package com.peekr.core.data.source.network.api

import com.peekr.core.data.source.network.dto.keywordGraph.response.NodeContextCursorPageResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 키워드 그래프 API
 */
interface KeywordGraphApi {
    /**
     * 노드 컨텍스트 페이지네이션 조회
     */
    @GET(NetworkApiPath.KeywordGraph.ROUTE)
    suspend fun getNodeContexts(
        @Query("userId") userId: Long,
        @Query("cursor") cursor: Long?,
        @Query("size") size: Int,
    ): Response<NodeContextCursorPageResponse>
}
