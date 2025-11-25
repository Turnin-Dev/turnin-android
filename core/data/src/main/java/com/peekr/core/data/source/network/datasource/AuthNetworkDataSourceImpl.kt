package com.peekr.core.data.source.network.datasource

import com.peekr.core.data.source.network.api.AuthApi
import com.peekr.core.data.source.network.dto.auth.request.ExistsUserRequest
import com.peekr.core.data.source.network.dto.auth.request.LoginRequest
import com.peekr.core.data.source.network.dto.auth.request.RegisterRequest
import com.peekr.core.data.source.network.dto.auth.response.ExistsResponse
import com.peekr.core.data.source.network.dto.auth.response.LoginResponse
import com.peekr.core.data.source.network.dto.auth.response.RegisterResponse
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.data.source.network.util.networkCall
import com.peekr.core.domain.model.DisplayId
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
