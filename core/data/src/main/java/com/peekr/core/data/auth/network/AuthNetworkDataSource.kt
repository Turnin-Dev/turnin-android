package com.peekr.core.data.auth.network

import com.peekr.core.data.auth.network.request.DisplayIdRequest
import com.peekr.core.data.auth.network.request.ExistsUserRequest
import com.peekr.core.data.auth.network.request.LoginRequest
import com.peekr.core.data.auth.network.request.RegisterRequest
import com.peekr.core.data.auth.network.response.ExistsResponse
import com.peekr.core.data.auth.network.response.LoginResponse
import com.peekr.core.data.auth.network.response.RegisterResponse
import com.peekr.core.data.network.util.NetworkResult
import com.peekr.core.data.network.util.networkCall
import javax.inject.Inject

class AuthNetworkDataSource @Inject constructor(
    private val authApi: AuthApi,
) : AuthDataSource {
    override suspend fun login(loginRequest: LoginRequest): NetworkResult<LoginResponse> =
        networkCall { authApi.login(loginRequest) }

    override suspend fun existsUser(existsUserRequest: ExistsUserRequest): NetworkResult<ExistsResponse> =
        networkCall { authApi.existsUser(existsUserRequest.provider, existsUserRequest.providerId) }

    override suspend fun existsDisplayId(displayIdRequest: DisplayIdRequest): NetworkResult<ExistsResponse> =
        networkCall { authApi.existsDisplayId(displayIdRequest.id) }

    override suspend fun register(registerRequest: RegisterRequest): NetworkResult<RegisterResponse> =
        networkCall { authApi.register(registerRequest) }
}
