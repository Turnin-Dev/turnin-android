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

/** Auth 네트워크 데이터소스 */
class AuthNetworkDataSource @Inject constructor(
    private val authApi: AuthApi,
) {
    /**
     * 로그인
     *
     * @param loginRequest 로그인 요청 바디
     * @return 성공 시 - [NetworkResult.Success]
     * @return 실패 시 - [NetworkResult.Error]
     */
    suspend fun login(loginRequest: LoginRequest): NetworkResult<LoginResponse> =
        networkCall { authApi.login(loginRequest) }

    /**
     * 사용자 존재 여부 확인
     *
     * @param existsUserRequest 사용자 존재 여부 확인 요청 바디
     * @return [ExistsResponse] - 존재하면 `ExistsResponse(true)`, 존재하지 않으면 `ExistsResponse(false)`
     */
    suspend fun existsUser(existsUserRequest: ExistsUserRequest): NetworkResult<ExistsResponse> =
        networkCall { authApi.existsUser(existsUserRequest.provider, existsUserRequest.providerId) }

    /**
     * 사용자 표시 ID 존재 여부 확인
     *
     * @param displayId 사용자 표시 ID
     * @return [ExistsResponse] - 존재하면 `ExistsResponse(true)`, 존재하지 않으면 `ExistsResponse(false)`
     */
    suspend fun existsDisplayId(displayId: DisplayId): NetworkResult<ExistsResponse> =
        networkCall { authApi.existsDisplayId(displayId.value) }

    /**
     * 회원가입
     *
     * @param registerRequest 회원가입 요청 바디
     */
    suspend fun register(registerRequest: RegisterRequest): NetworkResult<RegisterResponse> =
        networkCall { authApi.register(registerRequest) }
}
