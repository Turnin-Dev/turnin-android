package com.peekr.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.peekr.core.data.paging.PeekrCursorPagingSource
import com.peekr.core.data.source.network.datasource.KeywordGraphNetworkDataSource
import com.peekr.core.data.source.network.dto.keywordGraph.response.NodeContextResponse
import com.peekr.core.data.source.network.dto.keywordGraph.response.toDomainModel
import com.peekr.core.domain.keywordGraph.model.KeywordGraphPagingTokens
import com.peekr.core.domain.keywordGraph.model.NodeContext
import com.peekr.core.domain.keywordGraph.repository.KeywordGraphRepository
import com.peekr.core.domain.model.UserId
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class KeywordGraphRepositoryImpl @Inject constructor(
    private val keywordGraphNetworkDataSource: KeywordGraphNetworkDataSource,
) : KeywordGraphRepository {
    override fun getNodeContexts(
        userId: UserId,
        cursor: Long?,
        size: Int,
    ): Flow<PagingData<NodeContext>> {
        val pageSize = KeywordGraphPagingTokens.PAGE_SIZE

        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                initialLoadSize = pageSize,
            ),
            pagingSourceFactory = {
                PeekrCursorPagingSource<Long, NodeContextResponse>(
                    apiCall = { nextCursor ->
                        keywordGraphNetworkDataSource.getNodeContexts(
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
                pagingData.map(NodeContextResponse::toDomainModel)
            }
    }
}
