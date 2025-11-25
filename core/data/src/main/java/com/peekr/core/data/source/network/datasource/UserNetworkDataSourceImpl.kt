package com.peekr.core.data.source.network.datasource

import com.peekr.core.data.source.network.api.UserApi
import com.peekr.core.data.source.network.dto.user.request.UserPatchRequest
import com.peekr.core.data.source.network.dto.user.response.UserProfileResponse
import com.peekr.core.data.source.network.dto.user.response.UserResponse
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.data.source.network.util.networkCall
import com.peekr.core.data.source.network.util.networkCallWithoutResponse
import javax.inject.Inject

class UserNetworkDataSourceImpl @Inject constructor(
    private val userApi: UserApi,
) : UserNetworkDataSource {
    override suspend fun getUser(): NetworkResult<UserResponse> =
        networkCall { userApi.getUser() }

    override suspend fun getUserProfile(): NetworkResult<UserProfileResponse> =
        networkCall { userApi.getUserProfile() }

    override suspend fun updateUserById(patch: UserPatchRequest): NetworkResult<Unit> =
        networkCallWithoutResponse { userApi.updateUser(patch) }
}
