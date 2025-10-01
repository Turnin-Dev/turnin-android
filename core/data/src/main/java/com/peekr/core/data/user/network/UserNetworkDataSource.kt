package com.peekr.core.data.user.network

import com.peekr.core.data.network.util.NetworkResult
import com.peekr.core.data.network.util.networkCall
import com.peekr.core.data.network.util.networkCallWithoutResponse
import com.peekr.core.data.user.network.request.UserPatchRequest
import com.peekr.core.data.user.network.response.UserResponse
import com.peekr.core.domain.model.UserId
import javax.inject.Inject

class UserNetworkDataSource @Inject constructor(
    private val userApi: UserApi,
) : UserDataSource {
    override suspend fun getUserById(userId: UserId): NetworkResult<UserResponse> =
        networkCall { userApi.getUser(userId.value) }

    override suspend fun updateUserById(userId: UserId, patch: UserPatchRequest): NetworkResult<Unit> =
        networkCallWithoutResponse { userApi.updateUser(userId.value, patch) }
}
