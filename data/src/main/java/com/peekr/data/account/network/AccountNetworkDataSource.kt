package com.peekr.data.account.network

import com.peekr.data.account.model.request.LoginRequest
import com.peekr.data.account.model.response.LoginResponse
import com.peekr.data.shared.util.NetworkResult

/** Account 네트워크 데이터 소스 */
interface AccountNetworkDataSource {
    /**
 * 네트워크를 통해 로그인 요청을 수행합니다.
 *
 * @param loginRequest 로그인에 필요한 요청 정보.
 * @return 로그인 성공 시 [NetworkResult.Success]에 [LoginResponse]를 포함하여 반환하며, 실패 시 [NetworkResult.Error]를 반환합니다.
 */
    suspend fun login(loginRequest: LoginRequest): NetworkResult<LoginResponse>
}
