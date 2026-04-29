package com.turnin.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.turnin.core.data.paging.TurninCursorPagingSource
import com.turnin.core.data.source.network.datasource.DiscoverNetworkDataSource
import com.turnin.core.data.source.network.dto.discover.response.DiscoverContextResponse
import com.turnin.core.data.source.network.dto.discover.response.toDomainModel
import com.turnin.core.domain.discover.model.DiscoverContext
import com.turnin.core.domain.discover.model.DiscoverPagingTokens
import com.turnin.core.domain.discover.repository.DiscoverRepository
import com.turnin.core.domain.model.UserId
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DiscoverRepositoryImpl @Inject constructor(
    private val discoverNetworkDataSource: DiscoverNetworkDataSource,
) : DiscoverRepository {
    override fun getDiscoverContexts(
        userId: UserId,
    ): Flow<PagingData<DiscoverContext>> {
        val pageSize = DiscoverPagingTokens.PAGE_SIZE

        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                initialLoadSize = pageSize,
            ),
            pagingSourceFactory = {
                TurninCursorPagingSource<Long, DiscoverContextResponse>(
                    apiCall = { nextCursor ->
                        discoverNetworkDataSource.getDiscoverContexts(
                            userId = userId.value,
                            cursor = nextCursor,
                            size = pageSize,
                        )
                    },
                )
            },
        )
            .flow
            .map { pagingData ->
                pagingData.map(DiscoverContextResponse::toDomainModel)
            }
    }
}
