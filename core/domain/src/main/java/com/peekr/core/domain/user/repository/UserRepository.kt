package com.peekr.core.domain.user.repository

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.domain.model.Introduce
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.user.model.CoreMyProfile
import com.peekr.core.domain.user.model.CoreUserProfile
import com.peekr.core.domain.user.model.User
import com.peekr.core.domain.user.model.UserPatch
import kotlinx.coroutines.flow.Flow

/** 사용자 리포지토리 */
interface UserRepository {
    /**
     * 사용자 ID 조회
     */
    suspend fun getUserId(): UserId?

    /**
     * 사용자 조회
     *
     * @return [User]
     */
    fun getUser(): Flow<Result<User, CommonErrorType>>

    /**
     * 나의 프로필 정보를 로컬에서 조회한다.
     */
    fun getMyProfile(): Flow<CoreMyProfile?>

    /**
     * 나의 프로필 정보를 조회해서 로컬 데이터에 업데이트한다.
     */
    fun getMyProfileRefresh(): Flow<Result<Unit, CommonErrorType>>

    /**
     * 사용자 프로필 조회
     *
     * @param userId 사용자 ID
     * @param forceRefresh 강제 새로고침 (캐시를 무효화하고 데이터를 새롭게 받아온다.)
     *
     * @return [CoreUserProfile]
     */
    fun getUserProfile(
        userId: UserId,
        forceRefresh: Boolean = false,
    ): Flow<Result<CoreUserProfile, CommonErrorType>>

    /**
     * 나의 프로필 수정
     *
     * @param patch 사용자 수정 요청
     */
    fun updateMyProfile(patch: UserPatch): Flow<Result<Unit, CommonErrorType>>

    /**
     * 나의 소개글 수정
     *
     * @param introduce 소개글
     */
    fun updateMyIntroduce(introduce: Introduce): Flow<Result<Unit, CommonErrorType>>
}
