package com.peekr.core.data.source.network.datasource

import com.peekr.core.data.source.network.api.UserApi
import com.peekr.core.data.source.network.dto.user.request.UserPatchRequest
import com.peekr.core.data.source.network.dto.user.response.UserProfileResponse
import com.peekr.core.data.source.network.dto.user.response.UserResponse
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.data.source.network.util.networkCall
import com.peekr.core.data.source.network.util.networkCallWithoutResponse
import javax.inject.Inject

/** 사용자 데이터 소스 */
class UserNetworkDataSource @Inject constructor(
    private val userApi: UserApi,
) {
    /**
     * 사용자 조회
     *
     * @return [UserResponse]
     */
    suspend fun getUser(): NetworkResult<UserResponse> =
        networkCall { userApi.getUser() }

    /**
     * 사용자 프로필 조회
     *
     * @return [UserProfileResponse]
     */
    suspend fun getUserProfile(): NetworkResult<UserProfileResponse> =
        networkCall { userApi.getUserProfile() }

    /**
     * 사용자 수정
     *
     * @param patch 사용자 수정 요청 바디
     */
    suspend fun updateUserById(patch: UserPatchRequest): NetworkResult<Unit> =
        networkCallWithoutResponse { userApi.updateUser(patch) }
}
