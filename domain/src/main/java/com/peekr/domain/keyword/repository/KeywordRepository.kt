package com.peekr.domain.keyword.repository

import com.peekr.core.domain.model.KeywordId
import com.peekr.core.domain.util.ErrorType
import com.peekr.core.domain.util.Result
import com.peekr.domain.keyword.model.Keyword
import kotlinx.coroutines.flow.Flow

/** 키워드 리포지토리 */
interface KeywordRepository {
    /**
     * 키워드 조회
     *
     * @param keywordId 키워드 ID
     */
    fun getKeyword(keywordId: KeywordId): Flow<Result<Keyword, ErrorType>>

    /**
     * 키워드 생성
     *
     * @param keyword 키워드명
     */
    fun createKeyword(keyword: String): Flow<Result<Keyword, ErrorType>>
}
