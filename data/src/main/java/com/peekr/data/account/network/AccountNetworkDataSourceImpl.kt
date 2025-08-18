package com.peekr.data.account.network

import com.peekr.data.account.model.request.DisplayIdRequest
import com.peekr.data.account.model.request.ExistsUserRequest
import com.peekr.data.account.model.request.LoginRequest
import com.peekr.data.account.model.response.ExistsResponse
import com.peekr.data.account.model.response.LoginResponse
import com.peekr.data.shared.util.NetworkResult
import com.peekr.data.shared.util.network.networkCall
import javax.inject.Inject

/** Account 네트워크 데이터 소스 */
class AccountNetworkDataSourceImpl @Inject constructor(
    private val accountApi: AccountApi,
) : AccountNetworkDataSource {
    override suspend fun login(loginRequest: LoginRequest): NetworkResult<LoginResponse> =
        networkCall { accountApi.login(loginRequest) }

    override suspend fun existsUser(existsUserRequest: ExistsUserRequest): NetworkResult<ExistsResponse> =
        networkCall { accountApi.existsUser(existsUserRequest.provider, existsUserRequest.providerId) }

    override suspend fun existsDisplayId(displayId: DisplayIdRequest): NetworkResult<ExistsResponse> =
        networkCall { accountApi.existsDisplayId(displayId.id) }
}
