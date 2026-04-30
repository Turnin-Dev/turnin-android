package com.turnin.core.data.source.network.datasource

import com.turnin.core.data.source.network.api.AuthApi
import com.turnin.core.data.source.network.dto.auth.request.ExistsUserRequest
import com.turnin.core.data.source.network.dto.auth.request.LoginRequest
import com.turnin.core.data.source.network.dto.auth.request.RegisterRequest
import com.turnin.core.data.source.network.dto.auth.response.ExistsResponse
import com.turnin.core.data.source.network.dto.auth.response.LoginResponse
import com.turnin.core.data.source.network.dto.auth.response.RegisterResponse
import com.turnin.core.data.source.network.util.NetworkResult
import com.turnin.core.data.source.network.util.networkCall
import com.turnin.core.domain.model.DisplayId
import javax.inject.Inject

class AuthNetworkDataSourceImpl @Inject constructor(
    private val authApi: AuthApi,
) : AuthNetworkDataSource {
    override suspend fun login(loginRequest: LoginRequest): NetworkResult<LoginResponse> =
        networkCall { authApi.login(loginRequest) }

    override suspend fun existsUser(existsUserRequest: ExistsUserRequest): NetworkResult<ExistsResponse> =
        networkCall { authApi.existsUser(existsUserRequest.provider, existsUserRequest.providerId) }

    override suspend fun existsDisplayId(displayId: DisplayId): NetworkResult<ExistsResponse> =
        networkCall { authApi.existsDisplayId(displayId.value) }

    override suspend fun register(registerRequest: RegisterRequest): NetworkResult<RegisterResponse> =
        networkCall { authApi.register(registerRequest) }
}
