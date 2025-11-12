package com.peekr.core.data.userKeyword.network

import com.peekr.core.data.network.util.NetworkResult
import com.peekr.core.data.network.util.networkCall
import com.peekr.core.data.network.util.networkCallWithoutResponse
import com.peekr.core.data.userKeyword.network.request.CreateUserKeywordRequest
import com.peekr.core.data.userKeyword.network.request.PatchDescriptionRequest
import com.peekr.core.data.userKeyword.network.request.PatchOffsetRequest
import com.peekr.core.data.userKeyword.network.response.DescriptionResponse
import com.peekr.core.data.userKeyword.network.response.PatchDescriptionResponse
import com.peekr.core.data.userKeyword.network.response.PatchOffsetResponse
import com.peekr.core.data.userKeyword.network.response.UserKeywordResponse
import com.peekr.core.data.userKeyword.network.response.UserKeywordsResponse
import com.peekr.core.domain.model.UserKeywordId
import javax.inject.Inject

class UserKeywordNetworkDataSource @Inject constructor(
    private val userKeywordApi: UserKeywordApi,
) : UserKeywordDataSource {
    override suspend fun getUserKeywords(): NetworkResult<UserKeywordsResponse> =
        networkCall { userKeywordApi.getUserKeywords() }

    override suspend fun getDescription(
        userKeywordId: UserKeywordId,
    ): NetworkResult<DescriptionResponse> =
        networkCall { userKeywordApi.getDescription(userKeywordId.value) }

    override suspend fun createUserKeyword(
        createUserKeywordRequest: CreateUserKeywordRequest,
    ): NetworkResult<UserKeywordResponse> =
        networkCall { userKeywordApi.createUserKeyword(createUserKeywordRequest) }

    override suspend fun patchOffset(
        userKeywordId: UserKeywordId,
        patchOffsetRequest: PatchOffsetRequest,
    ): NetworkResult<PatchOffsetResponse> =
        networkCall {
            userKeywordApi.patchOffset(userKeywordId.value, patchOffsetRequest)
        }

    override suspend fun patchDescription(
        userKeywordId: UserKeywordId,
        patchDescriptionRequest: PatchDescriptionRequest,
    ): NetworkResult<PatchDescriptionResponse> =
        networkCall {
            userKeywordApi.patchDescription(userKeywordId.value, patchDescriptionRequest)
        }

    override suspend fun deleteUserKeyword(
        userKeywordId: UserKeywordId,
    ): NetworkResult<Unit> =
        networkCallWithoutResponse {
            userKeywordApi.deleteUserKeyword(userKeywordId.value)
        }
}
