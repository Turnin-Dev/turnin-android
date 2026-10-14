package com.turnin.core.data.source.network.api

import com.turnin.core.data.source.network.dto.auth.request.LoginRequest
import com.turnin.core.data.source.network.dto.auth.request.RegisterRequest
import com.turnin.core.data.source.network.dto.auth.response.ExistsResponse
import com.turnin.core.data.source.network.dto.auth.response.LoginResponse
import com.turnin.core.data.source.network.dto.auth.response.RegisterResponse
import com.turnin.core.data.source.network.retrofit.TokenResponse
import com.turnin.core.domain.model.SocialLoginProvider
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
