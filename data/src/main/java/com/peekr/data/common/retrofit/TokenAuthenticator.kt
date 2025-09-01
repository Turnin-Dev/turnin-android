package com.peekr.data.common.retrofit

import com.peekr.data.account.network.AccountApi
import com.peekr.data.common.retrofit.RetrofitConstants.AUTHENTICATION
import com.peekr.data.common.retrofit.RetrofitConstants.BEARER
import com.peekr.domain.common.dataStore.DataStoreKey
import com.peekr.domain.common.dataStore.DataStoreManager
import javax.inject.Inject
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber

/** 인증 요청 시 응답의 HTTP 상태코드가 401일 때만 호출된다.  */
class TokenAuthenticator @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val accountApi: AccountApi,
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? = runBlocking {
        Timber.d("TokenAuthenticator Triggered!")

        val newTokenResponse = refreshToken()

        // logging
        if (newTokenResponse.isSuccessful) {
            Timber.d("Token refresh successful (code: ${newTokenResponse.code()})")
        } else {
            Timber.w("Token refresh failed (code: ${newTokenResponse.code()})")
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
        accountApi.refresh()
}
