package com.peekr.data.account.network

import com.peekr.data.shared.retrofit.TokenResponse
import com.peekr.data.shared.util.NetworkApiPath
import retrofit2.Response
import retrofit2.http.GET

/** 토큰 새로고침 API */
interface RefreshTokenApi {
    /** 토큰 새로고침 */
    @GET("${NetworkApiPath.AUTH}/refresh")
    suspend fun refresh(): Response<TokenResponse>
}
