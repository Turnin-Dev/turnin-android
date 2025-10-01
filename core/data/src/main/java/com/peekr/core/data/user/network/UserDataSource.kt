package com.peekr.core.data.user.network

import com.peekr.core.data.network.util.NetworkResult
import com.peekr.core.data.user.network.request.UserPatchRequest
import com.peekr.core.data.user.network.response.UserResponse
import com.peekr.core.domain.model.UserId

/** 사용자 데이터 소스 */
interface UserDataSource {
    /**
     * 사용자 ID로 사용자 조회
     *
     * @param userId 사용자 ID
     *
     * @return [UserResponse]
     */
    suspend fun getUserById(userId: UserId): NetworkResult<UserResponse>

    /**
     * 사용자 수정
     *
     * @param userId 사용자 ID
     * @param patch 사용자 수정 요청 바디
     */
    suspend fun updateUserById(
        userId: UserId,
        patch: UserPatchRequest,
    ): NetworkResult<Unit>
}
