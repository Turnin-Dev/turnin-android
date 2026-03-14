package com.peekr.core.data.source.network.retrofit

import com.peekr.core.common.logger.AppLogger
import com.peekr.core.data.source.local.datastore.DataStoreKey
import com.peekr.core.data.source.local.datastore.DataStoreManager
import com.peekr.core.data.source.network.api.NetworkApiPath
import com.peekr.core.data.source.network.api.RefreshTokenApi
import com.peekr.core.data.source.network.retrofit.RetrofitConstants.AUTHENTICATION
import com.peekr.core.data.source.network.retrofit.RetrofitConstants.BEARER
import com.peekr.core.domain.eventBus.AuthEventBus
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import okio.IOException

/** 인증 요청 시 응답의 HTTP 상태코드가 401일 때만 호출된다.  */
class TokenAuthenticator(
    private val dataStoreManager: DataStoreManager,
    private val refreshTokenApi: RefreshTokenApi,
    private val authEventBus: AuthEventBus,
) : Authenticator {
    private val tag = this::class.java.simpleName

    private val mutex = Mutex()

    override fun authenticate(route: Route?, response: Response): Request? = runBlocking {
        AppLogger.d(tag, "TokenAuthenticator Triggered!")

        // 로그아웃 요청이라면 401 에러 무시
        val currentPath = response.request.url.encodedPath
        if (currentPath.contains(NetworkApiPath.User.LOGOUT)) {
            AppLogger.w(tag, "401 detected during logout. Stopping authentication loop.")
            return@runBlocking null // 루프 중단
        }

        // 1) Mutex 락
        mutex.withLock {
            // 2) 더블 체크 (현재 내 요청에 들어있는 토큰과 저장소의 토큰이 다르면 이미 갱신된 상태이므로 요청 그냥 진행)
            val currentAccessToken =
                dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.AccessToken).first()
            val requestToken = response.request.header(AUTHENTICATION)
            if (requestToken != "$BEARER $currentAccessToken" && currentAccessToken != null) {
                AppLogger.d(tag, "Token already refreshed by another thread. Using new token.")
                return@runBlocking response.request
                    .newBuilder()
                    .header(AUTHENTICATION, "$BEARER $currentAccessToken")
                    .build()
            }

            // 3) 리프레쉬 로직 실행
            val originalRefreshToken =
                dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.RefreshToken).first()
            if (originalRefreshToken == null) {
                AppLogger.w(tag, "No refresh token available")
                resetAuthDataAndLogout()
                return@runBlocking null
            }
            val newTokenResponse = try {
                refreshToken(originalRefreshToken)
            } catch (e: IOException) {
                // IO 작업(예: 네트워크 관련 작업) 예외는 단순 취소만 수행
                AppLogger.e(tag, e, "Token refresh request failed")
                return@runBlocking null
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                // IO 예외 이 외에는 보안상 토큰 삭제 후 로그아웃 처리
                AppLogger.e(tag, e, "Token refresh request failed")
                resetAuthDataAndLogout()
                return@runBlocking null
            }

            // 리프레쉬 실패 시 실패 처리(자동 로그아웃)
            if (!newTokenResponse.isSuccessful) {
                resetAuthDataAndLogout()
                AppLogger.w(tag, "Token refresh failed (code: ${newTokenResponse.code()})")
                return@runBlocking null
            }

            val body = newTokenResponse.body() ?: run {
                AppLogger.e(tag, "Token refresh body is null")
                resetAuthDataAndLogout()
                return@runBlocking null
            }

            // 토큰 저장
            dataStoreManager.saveEncryptedStringData(DataStoreKey.Auth.AccessToken, body.accessToken)
            dataStoreManager.saveEncryptedStringData(DataStoreKey.Auth.RefreshToken, body.refreshToken)

            AppLogger.d(tag, "Token refresh successful. Proceeding with new token.")

            // 새롭게 발급된 토큰으로 요청 진행
            response.request
                .newBuilder()
                .header(AUTHENTICATION, "$BEARER ${body.accessToken}")
                .build()
        }
    }

    /**
     * 토큰 헤더를 설정하고 RefreshToken API로 갱신 요청한다.
     */
    private suspend fun refreshToken(token: String): retrofit2.Response<TokenResponse> {
        val bearerToken = if (!token.trimStart().startsWith("$BEARER ", ignoreCase = true)) {
            "$BEARER $token"
        } else {
            token
        }
        return refreshTokenApi.refresh(bearerToken)
    }

    /**
     * 인증 실패 시 토큰을 전부 삭제하고 자동 로그아웃 처리를 한다.
     */
    private suspend fun resetAuthDataAndLogout() {
        AppLogger.d(tag, "Authentication Failure")
        dataStoreManager.deleteStringData(DataStoreKey.Auth.AccessToken)
        dataStoreManager.deleteStringData(DataStoreKey.Auth.RefreshToken)
        dataStoreManager.deleteLongData(DataStoreKey.User.UserId)

        authEventBus.emitLogout()
    }
}
