package com.peekr.data.userKeyword.network

import com.peekr.data.common.util.network.NetworkResult
import com.peekr.data.common.util.network.networkCall
import com.peekr.data.common.util.network.networkCallWithoutResponse
import com.peekr.data.userKeyword.model.request.CreateUserKeywordRequest
import com.peekr.data.userKeyword.model.request.PatchUserKeywordRequest
import com.peekr.data.userKeyword.model.response.UserKeywordResponse
import com.peekr.data.userKeyword.model.response.UserKeywordsResponse
import com.peekr.domain.common.model.UserId
import com.peekr.domain.common.model.UserKeywordId
import javax.inject.Inject

class UserKeywordDataSourceImpl @Inject constructor(
    private val userKeywordApi: UserKeywordApi,
) : UserKeywordDataSource {
    override suspend fun getUserKeywords(userId: UserId): NetworkResult<UserKeywordsResponse> =
        networkCall { userKeywordApi.getUserKeywords(userId.value) }

    override suspend fun createUserKeyword(
        createUserKeywordRequest: CreateUserKeywordRequest,
    ): NetworkResult<UserKeywordResponse> =
        networkCall { userKeywordApi.createUserKeyword(createUserKeywordRequest) }

    override suspend fun patchUserKeyword(
        ownerId: UserId,
        userKeywordId: UserKeywordId,
        patchUserKeywordRequest: PatchUserKeywordRequest,
    ): NetworkResult<Unit> =
        networkCallWithoutResponse {
            userKeywordApi.patchUserKeyword(ownerId.value, userKeywordId.value, patchUserKeywordRequest)
        }

    override suspend fun deleteUserKeyword(
        ownerId: UserId,
        userKeywordId: UserKeywordId,
    ): NetworkResult<Unit> =
        networkCallWithoutResponse {
            userKeywordApi.deleteUserKeyword(ownerId.value, userKeywordId.value)
        }
}
