package com.peekr.data.keyword.network

import com.peekr.data.common.util.network.NetworkResult
import com.peekr.data.common.util.network.networkCall
import com.peekr.data.keyword.model.request.CreateKeywordRequest
import com.peekr.data.keyword.model.response.KeywordResponse
import com.peekr.domain.common.model.KeywordId
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
