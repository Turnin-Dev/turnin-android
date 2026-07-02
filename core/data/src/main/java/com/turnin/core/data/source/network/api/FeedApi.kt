package com.turnin.core.data.source.network.api

import com.turnin.core.data.source.network.dto.feed.FeedCursorPageResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FeedApi {
    /**
     * 피드 조회 (커서 기반 페이지네이션)
     */
    @GET(NetworkApiPath.Feed.ROUTE)
    suspend fun getFeeds(
        @Query("cursorScore") cursorScore: Double?,
        @Query("cursorUserKeywordId") cursorUserKeywordId: Long?,
        @Query("size") size: Int,
    ): Response<FeedCursorPageResponse>
}
