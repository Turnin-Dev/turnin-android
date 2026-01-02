package com.peekr.core.data.source.network.datasource

import com.peekr.core.data.source.network.api.UserKeywordApi
import com.peekr.core.data.source.network.dto.userKeyword.request.CreateUserKeywordRequest
import com.peekr.core.data.source.network.dto.userKeyword.request.PatchDescriptionRequest
import com.peekr.core.data.source.network.dto.userKeyword.response.DescriptionResponse
import com.peekr.core.data.source.network.dto.userKeyword.response.PatchDescriptionResponse
import com.peekr.core.data.source.network.dto.userKeyword.response.UserKeywordResponse
import com.peekr.core.data.source.network.dto.userKeyword.response.UserKeywordsResponse
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.data.source.network.util.networkCall
import com.peekr.core.data.source.network.util.networkCallWithoutResponse
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.model.UserKeywordId
import javax.inject.Inject

class UserKeywordNetworkDataSourceImpl @Inject constructor(
    private val userKeywordApi: UserKeywordApi,
) : UserKeywordNetworkDataSource {
    override suspend fun getUserKeywords(userId: UserId): NetworkResult<UserKeywordsResponse> =
        networkCall { userKeywordApi.getUserKeywords(userId.value) }

    override suspend fun getDescription(
        userKeywordId: UserKeywordId,
    ): NetworkResult<DescriptionResponse> =
        networkCall { userKeywordApi.getDescription(userKeywordId.value) }

    override suspend fun createUserKeyword(
        createUserKeywordRequest: CreateUserKeywordRequest,
    ): NetworkResult<UserKeywordResponse> =
        networkCall { userKeywordApi.createUserKeyword(createUserKeywordRequest) }

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
