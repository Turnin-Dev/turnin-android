package com.peekr.core.data.source.network.datasource

import com.peekr.core.data.source.network.api.KeywordGraphApi
import com.peekr.core.data.source.network.dto.keywordGraph.response.NodeContextResponse
import com.peekr.core.data.source.network.util.CursorPageResponse
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.data.source.network.util.networkCall
import javax.inject.Inject

class KeywordGraphNetworkDataSourceImpl @Inject constructor(
    private val keywordGraphApi: KeywordGraphApi,
) : KeywordGraphNetworkDataSource {
    override suspend fun getNodeContexts(
        userId: Long,
        cursor: Long,
        size: Int,
    ): NetworkResult<CursorPageResponse<NodeContextResponse, Long>> =
        networkCall { keywordGraphApi.getNodeContexts(userId, cursor, size) }
}
