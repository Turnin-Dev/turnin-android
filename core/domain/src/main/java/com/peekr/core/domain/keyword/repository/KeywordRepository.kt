package com.peekr.core.domain.keyword.repository

import com.peekr.core.domain.keyword.model.Keyword
import com.peekr.core.domain.model.KeywordId
import com.peekr.core.domain.util.ErrorType
import com.peekr.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

/** 키워드 리포지토리 */
interface KeywordRepository {
    /**
     * 키워드 ID로 키워드 조회
     *
     * @param keywordId 키워드 ID
     */
    fun getKeywordById(keywordId: KeywordId): Flow<Result<Keyword?, ErrorType>>

    /**
     * 키워드 명로 키워드 조회
     *
     * @param keywordName 키워드 명
     */
    fun getKeywordByName(keywordName: String): Flow<Result<Keyword?, ErrorType>>

    /**
     * 키워드 생성
     *
     * @param keyword 키워드명
     */
    fun createKeyword(keyword: String): Flow<Result<Keyword, ErrorType>>
}
