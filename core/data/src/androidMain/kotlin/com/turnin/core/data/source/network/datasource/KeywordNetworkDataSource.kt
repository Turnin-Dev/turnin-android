package com.turnin.core.data.source.network.datasource

import com.turnin.core.data.source.network.dto.keyword.request.CreateKeywordRequest
import com.turnin.core.data.source.network.dto.keyword.response.KeywordResponse
import com.turnin.core.data.source.network.util.NetworkResult
import com.turnin.core.domain.model.KeywordId

/** Keyword 네트워크 데이터 소스 */

interface KeywordNetworkDataSource {
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
