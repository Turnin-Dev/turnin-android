package com.peekr.core.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.peekr.core.data.paging.FeedRemoteMediator
import com.peekr.core.data.source.local.database.PeekrDatabase
import com.peekr.core.data.source.local.database.entity.toDomainModel
import com.peekr.core.data.source.network.datasource.FeedNetworkDataSource
import com.peekr.core.domain.feed.model.Feed
import com.peekr.core.domain.feed.model.FeedCursor
import com.peekr.core.domain.feed.model.FeedPagingTokens
import com.peekr.core.domain.feed.repository.FeedRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalPagingApi::class)
class FeedRepositoryImpl @Inject constructor(
    private val feedNetworkDataSource: FeedNetworkDataSource,
    private val database: PeekrDatabase,
) : FeedRepository {
    override fun getFeeds(
        cursor: FeedCursor?,
        size: Int,
    ): Flow<PagingData<Feed>> {
        val pagingSourceFactory = { database.feedDao().getPagingSource() }
        val pageSize = FeedPagingTokens.PAGE_SIZE

        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                initialLoadSize = pageSize,
            ),
            remoteMediator = FeedRemoteMediator(
                feedNetworkDataSource = feedNetworkDataSource,
                database = database,
            ),
            pagingSourceFactory = pagingSourceFactory,
        )
            .flow
            .map { pagingData ->
                pagingData.map { feedEntity ->
                    feedEntity.toDomainModel()
                }
            }
    }
}
