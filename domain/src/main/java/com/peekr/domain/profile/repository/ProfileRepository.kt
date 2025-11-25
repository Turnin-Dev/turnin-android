package com.peekr.domain.profile.repository

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.model.KeywordValue
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.userKeyword.model.PatchDescription
import com.peekr.core.domain.userKeyword.model.PatchOffset
import com.peekr.core.domain.userKeyword.model.UserKeyword
import com.peekr.domain.profile.error.ProfileErrorType
import com.peekr.domain.profile.model.ProfilePatch
import kotlinx.coroutines.flow.Flow

/** 프로필 리포지토리 */
interface ProfileRepository {
    /**
     * 사용자 수정
     */
    fun updateProfile(patch: ProfilePatch): Flow<Result<Unit, ProfileErrorType>>

    /**
     * 키워드 추가
     *
     * 키워드 명으로 키워드를 조회하고 조회된 키워드가 만약 존재하지 않는다면
     * 새롭게 키워드 등록 후 사용자 키워드를 저장한다.
     *
     * 이미 존재하면 조회된 키워드로 사용자 키워드를 저장한다.
     *
     * @param keyword 키워드 명
     * @param offsetX 키워드 오프셋 X
     * @param offsetY 키워드 오프셋 Y
     * @param description 키워드 내용
     *
     * @return [UserKeyword]
     */
    fun addKeyword(
        keyword: KeywordValue,
        description: KeywordDescription,
        offsetX: Double,
        offsetY: Double,
    ): Flow<Result<UserKeyword, ProfileErrorType>>

    /**
     * 키워드 삭제
     *
     * @param userKeywordId 사용자 키워드 ID
     */
    fun deleteKeyword(
        userKeywordId: UserKeywordId,
    ): Flow<Result<Unit, ProfileErrorType>>

    /**
     * 사용자 키워드 오프셋 수정
     *
     * @param userKeywordId 사용자 키워드 ID
     * @param patchOffset 사용자 키워드 오프셋 수정 요청 객체
     */
    fun updateOffset(
        userKeywordId: UserKeywordId,
        patchOffset: PatchOffset,
    ): Flow<Result<PatchOffset, ProfileErrorType>>

    /**
     * 사용자 키워드 설명 수정
     *
     * @param userKeywordId 사용자 키워드 ID
     * @param patchDescription 사용자 키워드 설명 수정 요청 객체
     */
    fun updateDescription(
        userKeywordId: UserKeywordId,
        patchDescription: PatchDescription,
    ): Flow<Result<PatchDescription, ProfileErrorType>>
}
