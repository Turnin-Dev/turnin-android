package com.peekr.core.domain.userKeyword.repository

import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.userKeyword.model.CreateUserKeyword
import com.peekr.core.domain.userKeyword.model.PatchUserKeyword
import com.peekr.core.domain.userKeyword.model.UserKeyword
import com.peekr.core.domain.userKeyword.model.UserKeywords
import com.peekr.core.domain.util.ErrorType
import com.peekr.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

/** 사용자 키워드 리포지토리 */
interface UserKeywordRepository {
    /**
     * 사용자 키워드 리스트 조회
     */
    fun getUserKeywords(): Flow<Result<UserKeywords, ErrorType>>

    /**
     * 사용자 키워드 생성
     *
     * @param create 사용자 키워드 생성 요청 객체
     */
    fun createUserKeyword(create: CreateUserKeyword): Flow<Result<UserKeyword, ErrorType>>

    /**
     * 사용자 키워드 수정
     *
     * @param userKeywordId 사용자 키워드 ID
     * @param patch 사용자 키워드 수정 요청 객체
     */
    fun patchUserKeyword(
        userKeywordId: UserKeywordId,
        patch: PatchUserKeyword,
    ): Flow<Result<Unit, ErrorType>>

    /**
     * 사용자 키워드 삭제
     *
     * @param userKeywordId 사용자 키워드 ID
     */
    fun deleteUserKeyword(
        userKeywordId: UserKeywordId,
    ): Flow<Result<Unit, ErrorType>>
}
