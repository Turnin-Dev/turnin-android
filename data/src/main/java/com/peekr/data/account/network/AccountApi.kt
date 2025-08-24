package com.peekr.data.account.network

import com.peekr.data.account.model.request.LoginRequest
import com.peekr.data.account.model.response.ExistsResponse
import com.peekr.data.account.model.response.LoginResponse
import com.peekr.data.account.model.response.PresignedUrlResponse
import com.peekr.data.shared.retrofit.TokenResponse
import com.peekr.data.shared.util.NetworkApiPath
import com.peekr.domain.account.model.SocialLoginProvider
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

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

    /** 파일 업로드에 사용할 사전 정의된 URL 요청 */
    @GET(NetworkApiPath.File.UPLOAD)
    suspend fun getPresignedFileUploadUrl(
        @Query("fileName") fileName: String,
        @Query("mime") mime: String,
    ): Response<PresignedUrlResponse>
}
