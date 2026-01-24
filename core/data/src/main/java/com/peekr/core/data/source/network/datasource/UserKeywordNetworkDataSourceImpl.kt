package com.peekr.core.data.source.network.datasource

import com.peekr.core.data.source.network.api.UserKeywordApi
import com.peekr.core.data.source.network.dto.common.UserKeywordDetailResponse
import com.peekr.core.data.source.network.dto.userKeyword.request.CreateUserKeywordRequest
import com.peekr.core.data.source.network.dto.userKeyword.request.PatchUserKeywordRequest
import com.peekr.core.data.source.network.dto.userKeyword.response.UserKeywordResponse
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.data.source.network.util.networkCall
import com.peekr.core.data.source.network.util.networkCallWithoutResponse
import com.peekr.core.domain.model.UserKeywordId
import javax.inject.Inject

class UserKeywordNetworkDataSourceImpl @Inject constructor(
    private val userKeywordApi: UserKeywordApi,
) : UserKeywordNetworkDataSource {
    override suspend fun getDetail(
        userKeywordId: UserKeywordId,
    ): NetworkResult<UserKeywordDetailResponse> =
        networkCall { userKeywordApi.getDetail(userKeywordId.value) }

    override suspend fun createUserKeyword(
        createUserKeywordRequest: CreateUserKeywordRequest,
    ): NetworkResult<UserKeywordResponse> =
        networkCall { userKeywordApi.createUserKeyword(createUserKeywordRequest) }

    override suspend fun patch(
        patchUserKeywordRequest: PatchUserKeywordRequest,
    ): NetworkResult<Unit> =
        networkCallWithoutResponse {
            userKeywordApi.patch(patchUserKeywordRequest)
        }

    override suspend fun deleteUserKeyword(
        userKeywordId: UserKeywordId,
    ): NetworkResult<Unit> =
        networkCallWithoutResponse {
            userKeywordApi.deleteUserKeyword(userKeywordId.value)
        }
}
