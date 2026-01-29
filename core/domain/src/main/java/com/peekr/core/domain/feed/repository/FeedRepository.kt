package com.peekr.core.domain.feed.repository

import androidx.paging.PagingData
import com.peekr.core.domain.feed.model.Feed
import kotlinx.coroutines.flow.Flow

interface FeedRepository {
    /**
     * 피드 조회 (커서 페이지네이션)
     */
    fun getFeeds(): Flow<PagingData<Feed>>
}
