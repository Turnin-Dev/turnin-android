package com.peekr.core.data.keyword.network

import com.peekr.core.data.keyword.network.request.CreateKeywordRequest
import com.peekr.core.data.keyword.network.response.KeywordResponse
import com.peekr.core.data.network.util.NetworkResult
import com.peekr.core.domain.model.KeywordId

/** Keyword 네트워크 데이터 소스 */
interface KeywordDataSource {
    /**
     * 키워드 ID로 키워드 조회
     *
     * @param keywordId 키워드 ID
     */
    suspend fun getKeywordById(keywordId: KeywordId): NetworkResult<KeywordResponse>

    /**
     * 키워드 명으로 키워드 조회
     *
     * @param keywordName 키워드 명
     */
    suspend fun getKeywordByName(keywordName: String): NetworkResult<KeywordResponse>

    /**
     * 키워드 생성
     *
     * @param createKeywordRequest 키워드 생성 요청 바디
     */
    suspend fun createKeyword(
        createKeywordRequest: CreateKeywordRequest,
    ): NetworkResult<KeywordResponse>
}
