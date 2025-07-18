package com.peekr.data.account.network

import com.peekr.data.account.model.request.LoginRequest
import com.peekr.data.account.model.response.LoginResponse
import com.peekr.data.shared.util.NetworkResult
import com.peekr.data.shared.util.network.networkCall
import javax.inject.Inject

/** Account 네트워크 데이터 소스 */
class AccountNetworkDataSourceImpl @Inject constructor(
    private val accountApi: AccountApi,
) : AccountNetworkDataSource {
    /**
         * 로그인 요청을 수행하고 결과를 반환합니다.
         *
         * @param loginRequest 로그인에 필요한 사용자 정보가 포함된 요청 객체입니다.
         * @return 로그인 성공 시 사용자 정보가 포함된 응답, 실패 시 네트워크 오류 정보를 포함합니다.
         */
        override suspend fun login(loginRequest: LoginRequest): NetworkResult<LoginResponse> =
        networkCall { accountApi.login(loginRequest) }
}
