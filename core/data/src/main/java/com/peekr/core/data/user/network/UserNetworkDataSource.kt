package com.peekr.core.data.user.network

import com.peekr.core.data.network.util.NetworkResult
import com.peekr.core.data.network.util.networkCall
import com.peekr.core.data.network.util.networkCallWithoutResponse
import com.peekr.core.data.user.network.request.UserPatchRequest
import com.peekr.core.data.user.network.response.UserProfileResponse
import com.peekr.core.data.user.network.response.UserResponse
import javax.inject.Inject

class UserNetworkDataSource @Inject constructor(
    private val userApi: UserApi,
) : UserDataSource {
    override suspend fun getUser(): NetworkResult<UserResponse> =
        networkCall { userApi.getUser() }

    override suspend fun getUserProfile(): NetworkResult<UserProfileResponse> =
        networkCall { userApi.getUserProfile() }

    override suspend fun updateUserById(patch: UserPatchRequest): NetworkResult<Unit> =
        networkCallWithoutResponse { userApi.updateUser(patch) }
}
