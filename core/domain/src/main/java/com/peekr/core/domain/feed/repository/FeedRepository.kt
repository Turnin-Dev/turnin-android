package com.peekr.core.domain.feed.repository

import androidx.paging.PagingData
import com.peekr.core.domain.feed.model.Feed
import com.peekr.core.domain.feed.model.FeedCursor
import kotlinx.coroutines.flow.Flow

interface FeedRepository {
    /**
     * 피드 조회 (커서 페이지네이션)
     *
     * @param cursor 피드 커서
     * @param size 페이지 사이즈
     */
    fun getFeeds(
        cursor: FeedCursor?,
        size: Int,
    ): Flow<PagingData<Feed>>
}
