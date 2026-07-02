package com.turnin.core.data.source.network.datasource

import com.turnin.core.data.source.network.dto.feed.FeedCursorPageResponse
import com.turnin.core.data.source.network.util.NetworkResult

interface FeedNetworkDataSource {
    /**
     * 피드 조회 (커서 기반 페이지네이션)
     *
     * @param cursorScore 커서 값 1 (피드 점수)
     * @param cursorUserKeywordId 커서 값 2 (사용자 키워드 ID)
     * @param size 페이지 사이즈
     */
    suspend fun getFeeds(
        cursorScore: Double?,
        cursorUserKeywordId: Long?,
        size: Int,
    ): NetworkResult<FeedCursorPageResponse>
}
