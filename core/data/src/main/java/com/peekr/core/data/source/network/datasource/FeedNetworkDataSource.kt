package com.peekr.core.data.source.network.datasource

import com.peekr.core.data.source.network.dto.feed.FeedCursorPageResponse
import com.peekr.core.data.source.network.util.NetworkResult

interface FeedNetworkDataSource {
    /**
     * 피드 조회 (커서 기반 페이지네이션)
     *
     * @param cursorScore 커서 값 1 (피드 점수)
     * @param cursorCreatedAt 커서 값 2 (키워드 생성 일자)
     * @param cursorUserKeywordId 커서 값 3 (사용자 키워드 ID)
     * @param size 페이지 사이즈
     */
    suspend fun getFeeds(
        cursorScore: Double?,
        cursorCreatedAt: Long?,
        cursorUserKeywordId: Long?,
        size: Int,
    ): NetworkResult<FeedCursorPageResponse>
}
