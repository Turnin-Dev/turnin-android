package com.peekr.core.domain.userKeyword.repository

import com.peekr.core.domain.common.CommonErrorType
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.userKeyword.model.CreateUserKeyword
import com.peekr.core.domain.userKeyword.model.PatchDescription
import com.peekr.core.domain.userKeyword.model.PatchOffset
import com.peekr.core.domain.userKeyword.model.UserKeyword
import com.peekr.core.domain.userKeyword.model.UserKeywords
import kotlinx.coroutines.flow.Flow

/** 사용자 키워드 리포지토리 */
interface UserKeywordRepository {
    /**
     * 사용자 키워드 리스트 조회
     */
    fun getUserKeywords(): Flow<Result<UserKeywords, CommonErrorType>>

    /**
     * 사용자 키워드 설명 조회
     */
    fun getDescription(
        userKeywordId: UserKeywordId,
    ): Flow<Result<KeywordDescription, CommonErrorType>>

    /**
     * 사용자 키워드 생성
     *
     * @param create 사용자 키워드 생성 요청 객체
     */
    fun createUserKeyword(create: CreateUserKeyword): Flow<Result<UserKeyword, CommonErrorType>>

    /**
     * 사용자 키워드 오프셋 수정
     *
     * @param userKeywordId 사용자 키워드 ID
     * @param patchOffset 사용자 키워드 오프셋 수정 요청 객체
     */
    fun patchOffset(
        userKeywordId: UserKeywordId,
        patchOffset: PatchOffset,
    ): Flow<Result<PatchOffset, CommonErrorType>>

    /**
     * 사용자 키워드 설명 수정
     *
     * @param userKeywordId 사용자 키워드 ID
     * @param patchDescription 사용자 키워드 설명 수정 요청 객체
     */
    fun patchDescription(
        userKeywordId: UserKeywordId,
        patchDescription: PatchDescription,
    ): Flow<Result<PatchDescription, CommonErrorType>>

    /**
     * 사용자 키워드 삭제
     *
     * @param userKeywordId 사용자 키워드 ID
     */
    fun deleteUserKeyword(
        userKeywordId: UserKeywordId,
    ): Flow<Result<Unit, CommonErrorType>>
}
