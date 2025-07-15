package com.peekr.data.account.network

import com.peekr.data.account.model.request.LoginRequest
import com.peekr.data.account.model.response.LoginResponse
import com.peekr.data.shared.util.NetworkApiPath
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/** Account(Auth) API */
interface AccountApi {
    /** 소셜로그인 */
    @POST("${NetworkApiPath.AUTH}/login")
    suspend fun login(
        @Body loginRequest: LoginRequest,
    ): Response<LoginResponse>
}
