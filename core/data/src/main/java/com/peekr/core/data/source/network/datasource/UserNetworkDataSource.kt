package com.peekr.core.data.source.network.datasource

import com.peekr.core.data.source.network.dto.user.request.UserPatchRequest
import com.peekr.core.data.source.network.dto.user.response.UserProfileResponse
import com.peekr.core.data.source.network.dto.user.response.UserResponse
import com.peekr.core.data.source.network.util.NetworkResult

/** 사용자 데이터 소스 */
interface UserNetworkDataSource {
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
