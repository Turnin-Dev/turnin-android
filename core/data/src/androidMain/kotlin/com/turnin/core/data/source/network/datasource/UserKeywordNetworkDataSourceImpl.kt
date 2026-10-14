package com.turnin.core.data.source.network.datasource

import com.turnin.core.data.source.network.api.UserKeywordApi
import com.turnin.core.data.source.network.dto.common.UserKeywordDetailResponse
import com.turnin.core.data.source.network.dto.userKeyword.request.CreateUserKeywordRequest
import com.turnin.core.data.source.network.dto.userKeyword.request.PatchUserKeywordRequest
import com.turnin.core.data.source.network.dto.userKeyword.response.UserKeywordResponse
import com.turnin.core.data.source.network.util.NetworkResult
import com.turnin.core.data.source.network.util.networkCall
import com.turnin.core.data.source.network.util.networkCallWithoutResponse
import com.turnin.core.domain.model.UserKeywordId
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
