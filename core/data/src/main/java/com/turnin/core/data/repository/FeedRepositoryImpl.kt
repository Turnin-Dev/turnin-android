package com.turnin.core.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.turnin.core.data.paging.FeedRemoteMediator
import com.turnin.core.data.source.local.database.TurninDatabase
import com.turnin.core.data.source.local.database.entity.toDomainModel
import com.turnin.core.data.source.network.datasource.FeedNetworkDataSource
import com.turnin.core.domain.feed.model.Feed
import com.turnin.core.domain.feed.model.FeedPagingTokens
import com.turnin.core.domain.feed.model.FeedType
import com.turnin.core.domain.feed.repository.FeedRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalPagingApi::class)
class FeedRepositoryImpl @Inject constructor(
    private val feedNetworkDataSource: FeedNetworkDataSource,
    private val database: TurninDatabase,
) : FeedRepository {
    override fun getFeeds(type: FeedType): Flow<PagingData<Feed>> {
        val pagingSourceFactory = { database.feedDao().getPagingSource(type) }
        val pageSize = FeedPagingTokens.PAGE_SIZE

        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                initialLoadSize = pageSize,
            ),
            remoteMediator = FeedRemoteMediator(
                feedType = type,
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
