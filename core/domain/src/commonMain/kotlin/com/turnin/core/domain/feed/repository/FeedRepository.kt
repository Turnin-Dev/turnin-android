package com.turnin.core.domain.feed.repository

import androidx.paging.PagingData
import com.turnin.core.domain.feed.model.Feed
import com.turnin.core.domain.feed.model.FeedType
import kotlinx.coroutines.flow.Flow

interface FeedRepository {
    /**
     * 피드 조회 (커서 페이지네이션)
     *
     * @param type [FeedType]
     */
    fun getFeeds(type: FeedType): Flow<PagingData<Feed>>
}
