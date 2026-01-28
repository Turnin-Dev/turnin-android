package com.peekr.core.data.repository

import androidx.paging.PagingData
import com.peekr.core.data.source.network.datasource.FeedNetworkDataSource
import com.peekr.core.domain.feed.model.Feed
import com.peekr.core.domain.feed.model.FeedCursor
import com.peekr.core.domain.feed.repository.FeedRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class FeedRepositoryImpl @Inject constructor(
    private val feedNetworkDataSource: FeedNetworkDataSource,
) : FeedRepository {
    override fun getFeeds(cursor: FeedCursor?, size: Int): Flow<PagingData<Feed>> {
        TODO("Not yet implemented")
    }
}
