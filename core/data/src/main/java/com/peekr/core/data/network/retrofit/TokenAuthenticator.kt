package com.peekr.core.data.network.retrofit

import com.peekr.core.common.logger.AppLogger
import com.peekr.core.data.datastore.DataStoreKey
import com.peekr.core.data.datastore.DataStoreManager
import com.peekr.core.data.network.retrofit.RetrofitConstants.AUTHENTICATION
import com.peekr.core.data.network.retrofit.RetrofitConstants.BEARER
import javax.inject.Inject
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

/** 인증 요청 시 응답의 HTTP 상태코드가 401일 때만 호출된다.  */
class TokenAuthenticator @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val refreshTokenApi: RefreshTokenApi,
) : Authenticator {
    private val tag = this::class.java.simpleName

    override fun authenticate(route: Route?, response: Response): Request? = runBlocking {
        AppLogger.d(tag, "TokenAuthenticator Triggered!")

        val newTokenResponse = refreshToken()

        // logging
        if (newTokenResponse.isSuccessful) {
            AppLogger.d(tag, "Token refresh successful (code: ${newTokenResponse.code()})")
        } else {
            AppLogger.w(tag, "Token refresh failed (code: ${newTokenResponse.code()})")
        }

        // couldn't refresh the token, so restart the login process
        if (!newTokenResponse.isSuccessful || newTokenResponse.body() == null) {
            dataStoreManager.deleteStringData(DataStoreKey.Auth.AccessToken)
            dataStoreManager.deleteStringData(DataStoreKey.Auth.RefreshToken)
        }

        // Save tokens
        // & Call with tokens (can get response from 'chain.proceed(...)')
        newTokenResponse.body()?.let {
            dataStoreManager.saveEncryptedStringData(DataStoreKey.Auth.AccessToken, it.accessToken)
            dataStoreManager.saveEncryptedStringData(DataStoreKey.Auth.RefreshToken, it.refreshToken)
            response.request
                .newBuilder()
                .header(AUTHENTICATION, "$BEARER ${it.accessToken}")
                .build()
        }
    }

    private suspend fun refreshToken(): retrofit2.Response<TokenResponse> =
        refreshTokenApi.refresh()
}
