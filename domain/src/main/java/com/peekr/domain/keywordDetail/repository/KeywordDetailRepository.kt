package com.peekr.domain.keywordDetail.repository

import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.userKeyword.model.PatchDescription
import com.peekr.core.domain.util.Result
import com.peekr.domain.keywordDetail.error.KeywordDetailErrorType
import kotlinx.coroutines.flow.Flow

interface KeywordDetailRepository {
    /**
     * 사용자 ID 조회
     */
    fun getUserId(): Flow<Result<UserId, KeywordDetailErrorType>>

    /**
     * 키워드 설명 조회
     */
    fun getDescription(
        userKeywordId: Long,
    ): Flow<Result<KeywordDescription, KeywordDetailErrorType>>

    /**
     * 키워드 설명 업데이트
     *
     * @param userKeywordId 사용자 키워드 ID
     * @param description 키워드 설명
     *
     * @return [PatchDescription] 키워드 설명 수정 모델
     */
    fun updateDescription(
        userKeywordId: Long,
        description: String,
    ): Flow<Result<PatchDescription, KeywordDetailErrorType>>
}
