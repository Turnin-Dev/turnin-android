package com.peekr.core.data.userKeyword.network

import com.peekr.core.data.network.util.NetworkResult
import com.peekr.core.data.network.util.networkCall
import com.peekr.core.data.network.util.networkCallWithoutResponse
import com.peekr.core.data.userKeyword.network.request.CreateUserKeywordRequest
import com.peekr.core.data.userKeyword.network.request.PatchUserKeywordRequest
import com.peekr.core.data.userKeyword.network.response.UserKeywordResponse
import com.peekr.core.data.userKeyword.network.response.UserKeywordsResponse
import com.peekr.core.domain.model.UserKeywordId
import javax.inject.Inject

class UserKeywordNetworkDataSource @Inject constructor(
    private val userKeywordApi: UserKeywordApi,
) : UserKeywordDataSource {
    override suspend fun getUserKeywords(): NetworkResult<UserKeywordsResponse> =
        networkCall { userKeywordApi.getUserKeywords() }

    override suspend fun createUserKeyword(
        createUserKeywordRequest: CreateUserKeywordRequest,
    ): NetworkResult<UserKeywordResponse> =
        networkCall { userKeywordApi.createUserKeyword(createUserKeywordRequest) }

    override suspend fun patchUserKeyword(
        userKeywordId: UserKeywordId,
        patchUserKeywordRequest: PatchUserKeywordRequest,
    ): NetworkResult<Unit> =
        networkCallWithoutResponse {
            userKeywordApi.patchUserKeyword(userKeywordId.value, patchUserKeywordRequest)
        }

    override suspend fun deleteUserKeyword(
        userKeywordId: UserKeywordId,
    ): NetworkResult<Unit> =
        networkCallWithoutResponse {
            userKeywordApi.deleteUserKeyword(userKeywordId.value)
        }
}
