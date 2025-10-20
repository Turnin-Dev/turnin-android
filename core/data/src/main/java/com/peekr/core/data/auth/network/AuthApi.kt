package com.peekr.core.data.auth.network

import com.peekr.core.data.auth.network.request.LoginRequest
import com.peekr.core.data.auth.network.request.RegisterRequest
import com.peekr.core.data.auth.network.response.ExistsResponse
import com.peekr.core.data.auth.network.response.LoginResponse
import com.peekr.core.data.auth.network.response.RegisterResponse
import com.peekr.core.data.network.NetworkApiPath
import com.peekr.core.data.network.retrofit.TokenResponse
import com.peekr.core.domain.user.model.SocialLoginProvider
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/** Auth Network API */
interface AuthApi {
    /** 소셜 로그인 */
    @POST(NetworkApiPath.Auth.LOGIN)
    suspend fun login(
        @Body loginRequest: LoginRequest,
    ): Response<LoginResponse>

    /** 토큰 새로고침 */
    @GET(NetworkApiPath.Auth.REFRESH)
    suspend fun refresh(): Response<TokenResponse>

    /** 사용자 존재 여부 확인 */
    @GET("${NetworkApiPath.Auth.Exists.PROVIDER}/{provider}/{providerId}")
    suspend fun existsUser(
        @Path("provider") provider: SocialLoginProvider,
        @Path("providerId") providerId: String,
    ): Response<ExistsResponse>

    /** 사용자 표시 ID 존재 여부 확인 */
    @GET("${NetworkApiPath.Auth.Exists.DISPLAY_ID}/{displayId}")
    suspend fun existsDisplayId(
        @Path("displayId") displayId: String,
    ): Response<ExistsResponse>

    @POST(NetworkApiPath.Auth.REGISTER)
    suspend fun register(
        @Body registerRequest: RegisterRequest,
    ): Response<RegisterResponse>
}
