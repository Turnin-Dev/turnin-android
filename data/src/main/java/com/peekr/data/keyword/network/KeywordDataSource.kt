package com.peekr.data.keyword.network

import com.peekr.data.common.util.network.NetworkResult
import com.peekr.data.keyword.model.request.CreateKeywordRequest
import com.peekr.data.keyword.model.response.KeywordResponse
import com.peekr.domain.common.model.KeywordId

/** Keyword 네트워크 데이터 소스 */
interface KeywordDataSource {
    /**
     * 키워드 조회
     *
     * @param keywordId 키워드 ID
     */
    suspend fun getKeyword(keywordId: KeywordId): NetworkResult<KeywordResponse>

    /**
     * 키워드 생성
     *
     * @param createKeywordRequest 키워드 생성 요청 바디
     */
    suspend fun createKeyword(
        createKeywordRequest: CreateKeywordRequest,
    ): NetworkResult<KeywordResponse>
}
