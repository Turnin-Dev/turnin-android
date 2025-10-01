package com.peekr.core.data.keyword.network

import com.peekr.core.data.keyword.network.request.CreateKeywordRequest
import com.peekr.core.data.keyword.network.response.KeywordResponse
import com.peekr.core.data.network.util.NetworkResult
import com.peekr.core.data.network.util.networkCall
import com.peekr.core.domain.model.KeywordId
import javax.inject.Inject

class KeywordNetworkDataSource @Inject constructor(
    private val keywordApi: KeywordApi,
) : KeywordDataSource {
    override suspend fun getKeyword(keywordId: KeywordId): NetworkResult<KeywordResponse> =
        networkCall { keywordApi.getKeyword(keywordId.value) }

    override suspend fun createKeyword(
        createKeywordRequest: CreateKeywordRequest,
    ): NetworkResult<KeywordResponse> =
        networkCall { keywordApi.createKeyword(createKeywordRequest) }
}
