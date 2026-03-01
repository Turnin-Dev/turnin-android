package com.peekr.core.data.source.network.social

import android.content.Context
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.common.model.ApiError
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.model.KakaoSdkError
import com.kakao.sdk.user.UserApiClient
import com.peekr.core.common.coroutine.trySendAndClose
import com.peekr.core.common.logger.AppLogger
import com.peekr.core.domain.auth.social.SocialAuthManager
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.domain.model.ProviderId
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine

private typealias ProviderIdResult = Result<ProviderId, CommonErrorType>

class KakaoSocialAuthManager(private val context: Context) : SocialAuthManager {
    private val tag = this::class.java.simpleName

    override fun signIn(): Flow<Result<ProviderId, CommonErrorType>> = callbackFlow {
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
                        trySendAndClose(Result.Error(CommonErrorType.SocialAuth.KakaoSignInError))
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

    override suspend fun signOut(): Result<Unit, CommonErrorType> =
        suspendCancellableCoroutine { cont ->
            UserApiClient.instance.logout { e ->
                if (e == null) {
                    AppLogger.i(tag, "Kakao sign-out succeeded.")
                } else {
                    // 카카오 로그아웃은 실패해도 로컬 토큰이 삭제되므로 항상 성공으로 처리한다.
                    // https://developers.kakao.com/docs/latest/ko/kakaologin/android#logout
                    AppLogger.e(tag, e, "Kakao sign-out failed or already signed out.")
                }

                cont.resumeWith(kotlin.Result.success(Result.Success(Unit)))
            }
        }

    override suspend fun deleteAccount(): Result<Unit, CommonErrorType> =
        suspendCancellableCoroutine { cont ->
            UserApiClient.instance.unlink { e ->
                when {
                    // 성공한 경우
                    e == null -> {
                        AppLogger.i(tag, "Kakao account deleted.")
                        cont.resumeWith(kotlin.Result.success(Result.Success(Unit)))
                    }

                    // 등록된 카카오 계정이 없는 경우 (혹은 이미 탈퇴된 계정)
                    e is ApiError && e.response.code == -101 -> {
                        AppLogger.e(tag, e, "Kakao account already deleted.")
                        cont.resumeWith(kotlin.Result.success(Result.Success(Unit)))
                    }

                    // 토큰이 만료된 경우
                    e is KakaoSdkError && e.isInvalidTokenError() -> {
                        AppLogger.e(tag, e, "Kakao token invalid.")
                        cont.resumeWith(kotlin.Result.success(Result.Success(Unit)))
                    }

                    // 이 외 경우 (예: 네트워크 오류)
                    else -> {
                        AppLogger.e(tag, e, "Failed to delete Kakao account.")
                        cont.resumeWith(
                            kotlin.Result.success(
                                Result.Error(
                                    CommonErrorType.SocialAuth.KakaoDeleteAccountError,
                                    e.message,
                                ),
                            ),
                        )
                    }
                }
            }
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
                trySendAndClose(Result.Error(CommonErrorType.SocialAuth.Unexpected(error)))
            }
        }

    private fun ProducerScope<ProviderIdResult>.loginWithKakaoTalkError(
        context: Context,
        error: Throwable?,
    ) {
        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
            trySendAndClose(Result.Error(CommonErrorType.SocialAuth.Cancellation))
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
                trySendAndClose(Result.Error(CommonErrorType.SocialAuth.Unexpected(error)))
            }
        }

    private fun ProducerScope<ProviderIdResult>.loginWithKakaoAccountError(error: Throwable?) {
        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
            trySendAndClose(Result.Error(CommonErrorType.SocialAuth.Cancellation))
        } else {
            trySendAndClose(Result.Error(CommonErrorType.SocialAuth.KakaoSignInError, error?.message))
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
                trySendAndClose(Result.Error(CommonErrorType.SocialAuth.UserNotFound))
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
