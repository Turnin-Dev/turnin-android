package com.turnin.core.data.source.network.datasource

import com.turnin.core.data.source.network.api.DiscoverApi
import com.turnin.core.data.source.network.dto.discover.response.DiscoverContextCursorPageResponse
import com.turnin.core.data.source.network.util.NetworkResult
import com.turnin.core.data.source.network.util.networkCall
import javax.inject.Inject

class DiscoverNetworkDataSourceImpl @Inject constructor(
    private val discoverApi: DiscoverApi,
) : DiscoverNetworkDataSource {
    override suspend fun getDiscoverContexts(
        userId: Long,
        cursor: Long?,
        size: Int,
    ): NetworkResult<DiscoverContextCursorPageResponse> =
        networkCall { discoverApi.getDiscoverContexts(userId, cursor, size) }
}
