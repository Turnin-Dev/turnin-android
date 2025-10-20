package com.peekr.domain.profile.repository

import com.peekr.core.domain.keyword.model.KeywordValue
import com.peekr.core.domain.userKeyword.model.KeywordDescription
import com.peekr.core.domain.userKeyword.model.UserKeyword
import com.peekr.core.domain.util.ErrorType
import com.peekr.core.domain.util.Result
import com.peekr.domain.profile.model.Profile
import com.peekr.domain.profile.model.ProfilePatch
import kotlinx.coroutines.flow.Flow

/** 프로필 리포지토리 */
interface ProfileRepository {
    /**
     * 사용자 조회
     */
    fun getProfile(): Flow<Result<Profile, ErrorType>>

    /**
     * 사용자 수정
     */
    fun updateProfile(patch: ProfilePatch): Flow<Result<Unit, ErrorType>>

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
    ): Flow<Result<UserKeyword, ErrorType>>
}
