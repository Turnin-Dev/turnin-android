package com.peekr.core.data.user.network

import com.peekr.core.data.network.util.NetworkResult
import com.peekr.core.data.user.network.request.UserPatchRequest
import com.peekr.core.data.user.network.response.UserProfileResponse
import com.peekr.core.data.user.network.response.UserResponse

/** 사용자 데이터 소스 */
interface UserDataSource {
    /**
     * 사용자 조회
     *
     * @return [UserResponse]
     */
    suspend fun getUser(): NetworkResult<UserResponse>

    /**
     * 사용자 프로필 조회
     *
     * @return [UserProfileResponse]
     */
    suspend fun getUserProfile(): NetworkResult<UserProfileResponse>

    /**
     * 사용자 수정
     *
     * @param patch 사용자 수정 요청 바디
     */
    suspend fun updateUserById(patch: UserPatchRequest): NetworkResult<Unit>
}
