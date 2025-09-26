package com.peekr.core.data.network.retrofit

import com.peekr.core.data.network.NetworkApiPath
import retrofit2.Response
import retrofit2.http.GET

interface RefreshTokenApi {
    /** 토큰 새로고침 */
    @GET(NetworkApiPath.Auth.REFRESH)
    suspend fun refresh(): Response<TokenResponse>
}
