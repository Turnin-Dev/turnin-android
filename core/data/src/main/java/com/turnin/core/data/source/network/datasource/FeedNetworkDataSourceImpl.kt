package com.turnin.core.data.source.network.datasource

import com.turnin.core.data.source.network.api.FeedApi
import com.turnin.core.data.source.network.dto.feed.FeedCursorPageResponse
import com.turnin.core.data.source.network.util.NetworkResult
import com.turnin.core.data.source.network.util.networkCall
import com.turnin.core.domain.feed.model.FeedType
import javax.inject.Inject

class FeedNetworkDataSourceImpl @Inject constructor(
    private val feedApi: FeedApi,
) : FeedNetworkDataSource {
    override suspend fun getFeeds(
        feedType: FeedType,
        cursor: String?,
        size: Int,
    ): NetworkResult<FeedCursorPageResponse> =
        networkCall {
            feedApi.getFeeds(
                feedType = feedType,
                cursor = cursor,
                size = size,
            )
        }
}
