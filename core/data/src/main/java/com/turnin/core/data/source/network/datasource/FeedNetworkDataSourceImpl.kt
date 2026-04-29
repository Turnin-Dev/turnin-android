package com.turnin.core.data.source.network.datasource

import com.turnin.core.data.source.network.api.FeedApi
import com.turnin.core.data.source.network.dto.feed.FeedCursorPageResponse
import com.turnin.core.data.source.network.util.NetworkResult
import com.turnin.core.data.source.network.util.networkCall
import javax.inject.Inject

class FeedNetworkDataSourceImpl @Inject constructor(
    private val feedApi: FeedApi,
) : FeedNetworkDataSource {
    override suspend fun getFeeds(
        cursorScore: Double?,
        cursorCreatedAt: Long?,
        cursorUserKeywordId: Long?,
        size: Int,
    ): NetworkResult<FeedCursorPageResponse> =
        networkCall {
            feedApi.getFeeds(
                cursorScore = cursorScore,
                cursorCreatedAt = cursorCreatedAt,
                cursorUserKeywordId = cursorUserKeywordId,
                size = size,
            )
        }
}
