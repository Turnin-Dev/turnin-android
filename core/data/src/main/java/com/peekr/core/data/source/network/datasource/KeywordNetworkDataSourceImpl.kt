package com.peekr.core.data.source.network.datasource

import com.peekr.core.data.source.network.api.KeywordApi
import com.peekr.core.data.source.network.dto.keyword.request.CreateKeywordRequest
import com.peekr.core.data.source.network.dto.keyword.response.KeywordResponse
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.data.source.network.util.networkCall
import com.peekr.core.domain.model.KeywordId
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
