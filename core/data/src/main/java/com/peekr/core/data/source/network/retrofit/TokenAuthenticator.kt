package com.peekr.core.data.source.network.retrofit

import com.peekr.core.common.logger.AppLogger
import com.peekr.core.data.eventBus.AuthEventBus
import com.peekr.core.data.source.local.datastore.DataStoreKey
import com.peekr.core.data.source.local.datastore.DataStoreManager
import com.peekr.core.data.source.network.api.RefreshTokenApi
import com.peekr.core.data.source.network.retrofit.RetrofitConstants.AUTHENTICATION
import com.peekr.core.data.source.network.retrofit.RetrofitConstants.BEARER
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

/** 인증 요청 시 응답의 HTTP 상태코드가 401일 때만 호출된다.  */
class TokenAuthenticator(
    private val dataStoreManager: DataStoreManager,
    private val refreshTokenApi: RefreshTokenApi,
    private val authEventBus: AuthEventBus,
) : Authenticator {
    private val tag = this::class.java.simpleName

    override fun authenticate(route: Route?, response: Response): Request? = runBlocking {
        AppLogger.d(tag, "TokenAuthenticator Triggered!")

        // refresh token check
        val originalRefreshToken =
            dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.RefreshToken).first()
        if (originalRefreshToken == null) {
            handleAuthenticationFailure()
            return@runBlocking null
        }
        val newTokenResponse = refreshToken(originalRefreshToken)

        // logging
        if (newTokenResponse.isSuccessful) {
            AppLogger.d(tag, "Token refresh successful (code: ${newTokenResponse.code()})")
        } else {
            AppLogger.w(tag, "Token refresh failed (code: ${newTokenResponse.code()})")
        }

        // couldn't refresh the token, so restart the login process
        if (!newTokenResponse.isSuccessful) {
            handleAuthenticationFailure()
            return@runBlocking null
        }

        val body = newTokenResponse.body() ?: run {
            handleAuthenticationFailure()
            return@runBlocking null
        }

        // Save tokens
        // & Call with tokens (can get response from 'chain.proceed(...)')
        dataStoreManager.saveEncryptedStringData(DataStoreKey.Auth.AccessToken, body.accessToken)
        dataStoreManager.saveEncryptedStringData(DataStoreKey.Auth.RefreshToken, body.refreshToken)
        response.request
            .newBuilder()
            .header(AUTHENTICATION, "$BEARER ${body.accessToken}")
            .build()
    }

    private suspend fun refreshToken(token: String): retrofit2.Response<TokenResponse> {
        val bearerToken = if (!token.trimStart().startsWith("$BEARER ", ignoreCase = true)) {
            "$BEARER $token"
        } else {
            token
        }
        return refreshTokenApi.refresh(bearerToken)
    }

    private suspend fun handleAuthenticationFailure() {
        AppLogger.d(tag, "Authentication Failure")
        dataStoreManager.deleteStringData(DataStoreKey.Auth.AccessToken)
        dataStoreManager.deleteStringData(DataStoreKey.Auth.RefreshToken)
        dataStoreManager.deleteLongData(DataStoreKey.User.UserId)

        authEventBus.emitLogout()
    }
}
