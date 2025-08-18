package com.peekr.data.account.network

import com.peekr.data.account.model.request.LoginRequest
import com.peekr.data.account.model.response.ExistsResponse
import com.peekr.data.account.model.response.LoginResponse
import com.peekr.data.shared.retrofit.TokenResponse
import com.peekr.data.shared.util.NetworkApiPath
import com.peekr.domain.account.model.SocialLoginProvider
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/** Account(Auth) API */
interface AccountApi {
    /** 소셜 로그인 */
    @POST("${NetworkApiPath.Auth.ROUTE}/login")
    suspend fun login(
        @Body loginRequest: LoginRequest,
    ): Response<LoginResponse>

    /** 토큰 새로고침 */
    @GET("${NetworkApiPath.Auth.ROUTE}/refresh")
    suspend fun refresh(): Response<TokenResponse>

    /** 사용자 존재 여부 확인 */
    @GET("${NetworkApiPath.Auth.EXISTS.PROVIDER}/{provider}/{providerId}")
    suspend fun existsUser(
        @Path("provider") provider: SocialLoginProvider,
        @Path("providerId") providerId: String,
    ): Response<ExistsResponse>

    /** 사용자 표시 ID 존재 여부 확인 */
    @GET("${NetworkApiPath.Auth.EXISTS.DISPLAY_ID}/{displayId}")
    suspend fun existsDisplayId(
        @Path("displayId") displayId: String,
    ): Response<ExistsResponse>
}
