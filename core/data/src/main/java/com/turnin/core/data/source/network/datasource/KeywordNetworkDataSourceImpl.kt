package com.turnin.core.data.source.network.datasource

import com.turnin.core.data.source.network.api.KeywordApi
import com.turnin.core.data.source.network.dto.keyword.request.CreateKeywordRequest
import com.turnin.core.data.source.network.dto.keyword.response.KeywordResponse
import com.turnin.core.data.source.network.util.NetworkResult
import com.turnin.core.data.source.network.util.networkCall
import com.turnin.core.domain.model.KeywordId
import javax.inject.Inject

class KeywordNetworkDataSourceImpl @Inject constructor(
    private val keywordApi: KeywordApi,
) : KeywordNetworkDataSource {
    override suspend fun getKeywordById(keywordId: KeywordId): NetworkResult<KeywordResponse> =
        networkCall { keywordApi.getKeywordById(keywordId.value) }

    override suspend fun getKeywordByName(keywordName: String): NetworkResult<KeywordResponse> =
        networkCall { keywordApi.getKeywordByName(keywordName) }

    override suspend fun createKeyword(
        createKeywordRequest: CreateKeywordRequest,
    ): NetworkResult<KeywordResponse> =
        networkCall { keywordApi.createKeyword(createKeywordRequest) }
}
