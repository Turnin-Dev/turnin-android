package com.peekr.data.account.util

import android.content.Context
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.model.KakaoSdkError
import com.kakao.sdk.user.UserApiClient
import com.peekr.core.logger.AppLogger
import com.peekr.data.common.util.coroutine.trySendAndClose
import com.peekr.domain.account.model.ProviderId
import com.peekr.domain.account.util.AuthManager
import com.peekr.domain.common.util.ErrorType
import com.peekr.domain.common.util.Result
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

private typealias ProviderIdResult = Result<ProviderId, ErrorType>

class KakaoAuthManager(private val context: Context) : AuthManager {
    private val tag = this::class.java.simpleName

    override fun signIn(): Flow<Result<ProviderId, ErrorType>> = callbackFlow {
        if (AuthApiClient.instance.hasToken()) {
            UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                if (error != null) {
                    if (error is KakaoSdkError && error.isInvalidTokenError()) {
                        // 1. Login Required
                        AppLogger.i(tag, "Kakao login required")
                        login(context)
                    } else {
                        // 2. another error
                        AppLogger.i(tag, "Weird error during Kakao sign-in")
                        trySendAndClose(Result.Error(ErrorType.Auth.KakaoSignInError))
                    }
                } else {
                    // 3. token validity check successful (renew if necessary)
                    AppLogger.i(tag, "Kakao login required")
                    login(context)
                }
            }
        } else {
            // 1. Login Required
            AppLogger.i(tag, "Kakao login required")
            login(context)
        }

        awaitClose()
    }

    override fun signOut(): Flow<Result<Unit, ErrorType>> = callbackFlow {
        UserApiClient.instance.logout { e ->
            if (e == null) {
                AppLogger.i(tag, "Kakao sign-out succeeded.")
                trySendAndClose(Result.Success(Unit))
            } else {
                AppLogger.e(tag, e, "Kakao sign-out failed.")
                trySendAndClose(Result.Error(ErrorType.Auth.KakaoSignOutError, e.message))
            }
        }

        awaitClose()
    }

    override fun deleteAccount(): Flow<Result<Unit, ErrorType>> = callbackFlow {
        UserApiClient.instance.unlink { e ->
            if (e == null) {
                AppLogger.i(tag, "Kakao account deleted.")
                trySendAndClose(Result.Success(Unit))
            } else {
                AppLogger.e(tag, e, "Failed to delete Kakao account.")
                trySendAndClose(Result.Error(ErrorType.Auth.KakaoDeleteAccountError, e.message))
            }
        }

        awaitClose()
    }

    private fun ProducerScope<ProviderIdResult>.login(context: Context) {
        // 카카오톡 설치 확인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            loginWithKakaoTalk(context)
        } else {
            loginWithKakaoAccount(context)
        }
    }

    private fun ProducerScope<ProviderIdResult>.loginWithKakaoTalk(context: Context) =
        UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
            if (error != null) { // 로그인 실패/에러
                AppLogger.i(tag, "'Login with KakaoTalk' failed.")
                loginWithKakaoTalkError(context, error)
            } else if (token != null) { // 로그인 성공
                AppLogger.i(tag, "'Login with KakaoTalk' succeeded.")
                loginSuccess()
            } else {
                trySendAndClose(Result.Error(ErrorType.Unexpected(error)))
            }
        }

    private fun ProducerScope<ProviderIdResult>.loginWithKakaoTalkError(
        context: Context,
        error: Throwable?,
    ) {
        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
            trySendAndClose(Result.Error(ErrorType.Auth.Cancellation))
        } else {
            loginWithKakaoAccount(context)
        }
    }

    private fun ProducerScope<ProviderIdResult>.loginWithKakaoAccount(context: Context) =
        UserApiClient.instance.loginWithKakaoAccount(context) { token, error ->
            if (error != null) { // 로그인 실패/에러
                AppLogger.i(tag, "'Login with KakaoAccount' failed.")
                loginWithKakaoAccountError(error)
            } else if (token != null) { // 로그인 성공
                AppLogger.i(tag, "'Login with KakaoAccount' succeeded.")
                loginSuccess()
            } else {
                trySendAndClose(Result.Error(ErrorType.Unexpected(error)))
            }
        }

    private fun ProducerScope<ProviderIdResult>.loginWithKakaoAccountError(error: Throwable?) {
        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
            trySendAndClose(Result.Error(ErrorType.Auth.Cancellation))
        } else {
            close()
        }
    }

    private fun ProducerScope<ProviderIdResult>.loginSuccess() {
        UserApiClient.instance.me { user, error ->
            if (user?.id != null) {
                AppLogger.i(tag, "Kakao Login succeeded")
                val providerId = ProviderId(user.id.toString())
                trySendAndClose(Result.Success(providerId))
            } else {
                AppLogger.i(tag, "Kakao user not found.")
                trySendAndClose(Result.Error(ErrorType.Auth.UserNotFound))
            }
        }
    }

    // 이메일 로그인 필요 시 활성화
//    private val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
//        if (error != null) {
//            // Login Failed
//        } else if (token != null) {
//            // Login Success (ex -> token.accessToken)
//        }
//    }
}
