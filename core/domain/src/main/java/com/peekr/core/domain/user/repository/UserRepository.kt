package com.peekr.core.domain.user.repository

import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.user.error.UserErrorType
import com.peekr.core.domain.user.model.User
import com.peekr.core.domain.user.model.UserPatch
import com.peekr.core.domain.user.model.UserProfile
import com.peekr.core.domain.util.Result
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
     * 사용자 프로필 조회
     *
     * @return [UserProfile]
     */
    fun getUserProfile(): Flow<Result<UserProfile, UserErrorType>>

    /**
     * 사용자 수정
     *
     * @param patch 사용자 수정 요청
     */
    fun updateUser(patch: UserPatch): Flow<Result<Unit, UserErrorType>>
}
