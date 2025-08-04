package com.peekr.data.account.network

import com.peekr.data.account.model.request.ExistsUserRequest
import com.peekr.data.account.model.request.LoginRequest
import com.peekr.data.account.model.response.ExistsUserResponse
import com.peekr.data.account.model.response.LoginResponse
import com.peekr.data.shared.util.NetworkResult

/** Account 네트워크 데이터 소스 */
interface AccountNetworkDataSource {
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
     * @return 존재하는 사용자 - `NetworkResult.Success(true)`
     * @return 존재하지 않는 사용자 - `NetworkResult.Success(false)`
     */
    suspend fun existsUser(existsUserRequest: ExistsUserRequest): NetworkResult<ExistsUserResponse>
}
