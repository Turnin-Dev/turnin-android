package com.peekr.core.data.source.network.social

import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.common.model.ApiError
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

class KakaoSocialAuthManager : SocialAuthManager {
    private val tag = this::class.java.simpleName

    // SDK 로그인 없이 기존 토큰으로 로그인 시도
    // 토큰이 없거나 만료된 경우 KakaoLoginRequired 반환
    // → LoginRoute에서 SDK 로그인 성공 후 loginViewModel.login() 재호출 → signIn() 재진입 → fetchUser()
    override fun signIn(): Flow<Result<ProviderId, CommonErrorType>> = callbackFlow {
        if (AuthApiClient.instance.hasToken()) {
            UserApiClient.instance.accessTokenInfo { _, error ->
                if (error != null) {
                    if (error is KakaoSdkError && error.isInvalidTokenError()) {
                        // 토큰 만료 → SDK 로그인 필요
                        AppLogger.i(tag, "Kakao token invalid, login required")
                        trySendAndClose(Result.Error(CommonErrorType.SocialAuth.KakaoLoginRequired))
                    } else {
                        // 토큰 유효성 확인 중 알 수 없는 오류
                        AppLogger.i(tag, "Weird error during Kakao sign-in")
                        trySendAndClose(Result.Error(CommonErrorType.SocialAuth.KakaoSignInError))
                    }
                } else {
                    // 토큰 유효 → 사용자 정보 조회
                    AppLogger.i(tag, "Kakao token valid, fetching user")
                    fetchUser()
                }
            }
        } else {
            // 저장된 토큰 없음 → SDK 로그인 필요
            AppLogger.i(tag, "Kakao token not found, login required")
            trySendAndClose(Result.Error(CommonErrorType.SocialAuth.KakaoLoginRequired))
        }

        awaitClose()
    }

    // 카카오 사용자 정보 조회 후 ProviderId 반환
    // signIn() 내부에서만 호출 (토큰 유효 시 또는 SDK 로그인 완료 후 재진입 시)
    private fun ProducerScope<ProviderIdResult>.fetchUser() {
        UserApiClient.instance.me { user, error ->
            if (user?.id != null) {
                AppLogger.i(tag, "Kakao Login succeeded")
                trySendAndClose(Result.Success(ProviderId(user.id.toString())))
            } else {
                AppLogger.i(tag, "Kakao user not found.")
                trySendAndClose(Result.Error(CommonErrorType.SocialAuth.UserNotFound))
            }
        }
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

    // 이메일 로그인 필요 시 활성화
//    private val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
//        if (error != null) {
//            // Login Failed
//        } else if (token != null) {
//            // Login Success (ex -> token.accessToken)
//        }
//    }
}
