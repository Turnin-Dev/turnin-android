package com.peekr.core.data.source.network.datasource

import com.peekr.core.data.source.network.dto.auth.request.ExistsUserRequest
import com.peekr.core.data.source.network.dto.auth.request.LoginRequest
import com.peekr.core.data.source.network.dto.auth.request.RegisterRequest
import com.peekr.core.data.source.network.dto.auth.response.ExistsResponse
import com.peekr.core.data.source.network.dto.auth.response.LoginResponse
import com.peekr.core.data.source.network.dto.auth.response.RegisterResponse
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.domain.model.DisplayId

/** Auth 네트워크 데이터소스 */
interface AuthNetworkDataSource {
    /**
     * 로그인
     *
     * @param loginRequest 로그인 요청 바디
     * @return 성공 시 - [NetworkResult.Success]
     * @return 실패 시 - [NetworkResult.Error]
     */
    suspend fun login(loginRequest: LoginRequest): NetworkResult<LoginResponse>

    /**
     * 사용자 존재 여부 확인
     *
     * @param existsUserRequest 사용자 존재 여부 확인 요청 바디
     * @return [ExistsResponse] - 존재하면 `ExistsResponse(true)`, 존재하지 않으면 `ExistsResponse(false)`
     */
    suspend fun existsUser(existsUserRequest: ExistsUserRequest): NetworkResult<ExistsResponse>

    /**
     * 사용자 표시 ID 존재 여부 확인
     *
     * @param displayId 사용자 표시 ID
     * @return [ExistsResponse] - 존재하면 `ExistsResponse(true)`, 존재하지 않으면 `ExistsResponse(false)`
     */
    suspend fun existsDisplayId(displayId: DisplayId): NetworkResult<ExistsResponse>

    /**
     * 회원가입
     *
     * @param registerRequest 회원가입 요청 바디
     */
    suspend fun register(registerRequest: RegisterRequest): NetworkResult<RegisterResponse>
}
