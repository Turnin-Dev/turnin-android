package com.peekr.core.data.source.network.datasource

import com.peekr.core.data.source.network.api.UserApi
import com.peekr.core.data.source.network.dto.common.UserKeywordDetailResponse
import com.peekr.core.data.source.network.dto.user.request.IntroducePatchRequest
import com.peekr.core.data.source.network.dto.user.request.UserPatchRequest
import com.peekr.core.data.source.network.dto.user.response.MyProfileResponse
import com.peekr.core.data.source.network.dto.user.response.UserProfileResponse
import com.peekr.core.data.source.network.dto.user.response.UserResponse
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.data.source.network.util.networkCall
import com.peekr.core.data.source.network.util.networkCallWithoutResponse
import com.peekr.core.domain.model.UserId
import javax.inject.Inject

class UserNetworkDataSourceImpl @Inject constructor(
    private val userApi: UserApi,
) : UserNetworkDataSource {
    override suspend fun getUser(): NetworkResult<UserResponse> =
        networkCall { userApi.getUser() }

    override suspend fun getMyProfile(): NetworkResult<MyProfileResponse> =
        networkCall { userApi.getMyProfile() }

    override suspend fun getUserProfile(userId: UserId): NetworkResult<UserProfileResponse> =
        networkCall { userApi.getUserProfile(userId.value) }

    override suspend fun getMyKeywords(): NetworkResult<List<UserKeywordDetailResponse>> =
        networkCall { userApi.getMyKeywords() }

    override suspend fun getUserKeywords(
        userId: Long,
    ): NetworkResult<List<UserKeywordDetailResponse>> =
        networkCall { userApi.getUserKeywords(userId) }

    override suspend fun updateUser(patch: UserPatchRequest): NetworkResult<Unit> =
        networkCallWithoutResponse { userApi.updateUser(patch) }

    override suspend fun updateIntroduce(patch: IntroducePatchRequest): NetworkResult<Unit> =
        networkCallWithoutResponse { userApi.updateIntroduce(patch) }
}
