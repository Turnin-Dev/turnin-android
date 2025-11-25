package com.peekr.core.data.source.network.api

import com.peekr.core.data.source.network.retrofit.TokenResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface RefreshTokenApi {
    /** 토큰 새로고침 */
    @GET(NetworkApiPath.Auth.REFRESH)
    suspend fun refresh(
        @Header("Authorization") refreshToken: String,
    ): Response<TokenResponse>
}
