package com.peekr.data.keyword.network

import com.peekr.core.data.network.util.NetworkResult
import com.peekr.core.data.network.util.networkCall
import com.peekr.core.domain.model.KeywordId
import com.peekr.data.keyword.model.request.CreateKeywordRequest
import com.peekr.data.keyword.model.response.KeywordResponse
import javax.inject.Inject

class KeywordDataSourceImpl @Inject constructor(
    private val keywordApi: KeywordApi,
) : KeywordDataSource {
    override suspend fun getKeyword(keywordId: KeywordId): NetworkResult<KeywordResponse> =
        networkCall { keywordApi.getKeyword(keywordId.value) }

    override suspend fun createKeyword(
        createKeywordRequest: CreateKeywordRequest,
    ): NetworkResult<KeywordResponse> =
        networkCall { keywordApi.createKeyword(createKeywordRequest) }
}
