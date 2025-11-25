package com.peekr.core.domain.user.repository

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.model.Introduce
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.user.error.UserErrorType
import com.peekr.core.domain.user.model.MyProfile
import com.peekr.core.domain.user.model.User
import com.peekr.core.domain.user.model.UserPatch
import com.peekr.core.domain.user.model.UserProfile
import kotlinx.coroutines.flow.Flow

/** 사용자 리포지토리 */
interface UserRepository {
    /**
     * 사용자 ID 조회
     */
    fun getUserId(): Flow<Result<UserId, UserErrorType>>

    /**
     * 사용자 조회
     *
     * @return [User]
     */
    fun getUser(): Flow<Result<User, UserErrorType>>

    /**
     * 나의 프로필 조회
     *
     * @return [MyProfile]
     */
    fun getMyProfile(): Flow<Result<MyProfile, UserErrorType>>

    /**
     * 사용자 프로필 조회
     *
     * @return [UserProfile]
     */
    fun getUserProfile(userId: UserId): Flow<Result<UserProfile, UserErrorType>>

    /**
     * 사용자 수정
     *
     * @param patch 사용자 수정 요청
     */
    fun updateUser(patch: UserPatch): Flow<Result<Unit, UserErrorType>>

    /**
     * 사용자 소개글 수정
     *
     * @param introduce 소개글
     */
    fun updateIntroduce(introduce: Introduce): Flow<Result<Unit, UserErrorType>>
}
