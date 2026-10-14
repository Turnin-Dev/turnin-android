package com.turnin.core.data.source.network.datasource

import com.turnin.core.data.source.network.dto.feed.FeedCursorPageResponse
import com.turnin.core.data.source.network.util.NetworkResult
import com.turnin.core.domain.feed.model.FeedType

interface FeedNetworkDataSource {
    /**
     * 피드 조회 (커서 기반 페이지네이션)
     *
     * @param feedType 피드 유형
     * @param cursor 커서
     * @param size 페이지 사이즈
     */
    suspend fun getFeeds(
        feedType: FeedType,
        cursor: String?,
        size: Int,
    ): NetworkResult<FeedCursorPageResponse>
}
