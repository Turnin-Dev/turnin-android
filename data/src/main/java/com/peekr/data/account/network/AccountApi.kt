package com.peekr.data.account.network

import com.peekr.data.account.model.request.LoginRequest
import com.peekr.data.account.model.response.ExistsUserResponse
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
    /** 소셜로그인 */
    @POST("${NetworkApiPath.AUTH}/login")
    suspend fun login(
        @Body loginRequest: LoginRequest,
    ): Response<LoginResponse>

    /** 토큰 새로고침 */
    @GET("${NetworkApiPath.AUTH}/refresh")
    suspend fun refresh(): Response<TokenResponse>

    @GET("${NetworkApiPath.AUTH}/exist/{provider}/{providerId}")
    suspend fun existsUser(
        @Path("provider") provider: SocialLoginProvider,
        @Path("provierId") providerId: String,
    ): Response<ExistsUserResponse>
}
