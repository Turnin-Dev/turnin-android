package com.peekr.data.account.util

import android.content.Context
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.model.KakaoSdkError
import com.kakao.sdk.user.UserApiClient
import com.peekr.data.shared.util.trySendAndClose
import com.peekr.domain.account.model.UserUID
import com.peekr.domain.account.util.AuthManager
import com.peekr.domain.shared.util.ErrorType
import com.peekr.domain.shared.util.Result
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

private typealias UserUIDResult = Result<UserUID, ErrorType>

class KakaoAuthManager(private val context: Context) : AuthManager {
    override suspend fun signIn(): Flow<Result<UserUID, ErrorType>> = callbackFlow {
        if (AuthApiClient.instance.hasToken()) {
            UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                if (error != null) {
                    if (error is KakaoSdkError && error.isInvalidTokenError()) {
                        // 1. Login Required
                        login(context)
                    } else {
                        // 2. another error
                        trySendAndClose(Result.Error(ErrorType.Auth.KakaoSignInError))
                    }
                } else {
                    // 3. token validity check successful (renew if necessary)
                    login(context)
                }
            }
        } else {
            // 1. Login Required
            login(context)
        }

        awaitClose()
    }

    override suspend fun signOut(): Flow<Result<Unit, ErrorType>> = callbackFlow {
        UserApiClient.instance.logout { e ->
            if (e == null) {
                trySendAndClose(Result.Success(Unit))
            } else {
                trySendAndClose(Result.Error(ErrorType.Auth.KakaoSignOutError, e.message))
            }
        }

        awaitClose()
    }

    override suspend fun deleteAccount(): Flow<Result<Unit, ErrorType>> = callbackFlow {
        UserApiClient.instance.unlink { e ->
            if (e == null) {
                trySendAndClose(Result.Success(Unit))
            } else {
                trySendAndClose(Result.Error(ErrorType.Auth.KakaoDeleteAccountError, e.message))
            }
        }

        awaitClose()
    }

    private fun ProducerScope<UserUIDResult>.login(context: Context) {
        // 카카오톡 설치 확인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            loginWithKakaoTalk(context)
        } else {
            loginWithKakaoAccount(context)
        }
    }

    private fun ProducerScope<UserUIDResult>.loginWithKakaoTalk(context: Context) =
        UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
            if (error != null) { // 로그인 실패/에러
                loginWithKakaoTalkError(context, error)
            } else if (token != null) { // 로그인 성공
                loginSuccess()
            } else {
                trySendAndClose(Result.Error(ErrorType.Auth.Unexpected))
            }
        }

    private fun ProducerScope<UserUIDResult>.loginWithKakaoTalkError(
        context: Context,
        error: Throwable?,
    ) {
        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
            trySendAndClose(Result.Error(ErrorType.Auth.Cancellation))
        } else {
            loginWithKakaoAccount(context)
        }
    }

    private fun ProducerScope<UserUIDResult>.loginWithKakaoAccount(context: Context) =
        UserApiClient.instance.loginWithKakaoAccount(context) { token, error ->
            if (error != null) { // 로그인 실패/에러
                loginWithKakaoAccountError(error)
            } else if (token != null) { // 로그인 성공
                loginSuccess()
            } else {
                trySendAndClose(Result.Error(ErrorType.Auth.Unexpected))
            }
        }

    private fun ProducerScope<UserUIDResult>.loginWithKakaoAccountError(error: Throwable?) {
        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
            trySendAndClose(Result.Error(ErrorType.Auth.Cancellation))
        } else {
            close()
        }
    }

    private fun ProducerScope<UserUIDResult>.loginSuccess() {
        UserApiClient.instance.me { user, error ->
            if (user?.id != null) {
                val userUID = UserUID(user.id.toString())
                trySendAndClose(Result.Success(userUID))
            } else {
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
