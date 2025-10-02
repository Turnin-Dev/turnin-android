package com.peekr.core.domain.user.repository

import com.peekr.core.domain.user.model.User
import com.peekr.core.domain.user.model.UserPatch
import com.peekr.core.domain.util.ErrorType
import com.peekr.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

/** 사용자 리포지토리 */
interface UserRepository {
    /**
     * 사용자 조회
     *
     * @return [User]
     */
    fun getUserById(): Flow<Result<User, ErrorType>>

    /**
     * 사용자 수정
     *
     * @param patch 사용자 수정 요청
     */
    fun updateUserById(patch: UserPatch): Flow<Result<Unit, ErrorType>>
}
