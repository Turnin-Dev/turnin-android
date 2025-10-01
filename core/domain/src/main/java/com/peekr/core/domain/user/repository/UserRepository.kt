package com.peekr.core.domain.user.repository

import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.user.model.User
import com.peekr.core.domain.user.model.UserPatch
import com.peekr.core.domain.util.ErrorType
import com.peekr.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

/** 사용자 리포지토리 */
interface UserRepository {
    /**
     * 사용자 ID로 사용자 조회
     *
     * @param userId 사용자 ID
     *
     * @return [User]
     */
    fun getUserById(userId: UserId): Flow<Result<User, ErrorType>>

    /**
     * 사용자 수정
     *
     * @param userId 사용자 ID
     * @param patch 사용자 수정 요청
     */
    fun updateUserById(
        userId: UserId,
        patch: UserPatch,
    ): Flow<Result<Unit, ErrorType>>
}
