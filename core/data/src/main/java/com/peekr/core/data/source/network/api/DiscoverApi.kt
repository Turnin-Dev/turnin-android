package com.peekr.core.data.source.network.api

import com.peekr.core.data.source.network.dto.discover.response.DiscoverContextCursorPageResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 탐색 API
 */
interface DiscoverApi {
    @GET(NetworkApiPath.Discover.ROUTE)
    /**
     * 탐색 컨텍스트 페이지네이션 조회
     *
     * 탐색 컨텍스트: 사용자 정보 일부 + 키워드 정보 일부
     */
    suspend fun getDiscoverContexts(
        @Query("userId") userId: Long,
        @Query("cursor") cursor: Long?,
        @Query("size") size: Int,
    ): Response<DiscoverContextCursorPageResponse>
}
