package com.peekr.presentation.login

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.peekr.core.common.logger.AppLogger
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.presentation.common.error.asUiText
import com.peekr.core.presentation.common.util.LaunchedUiEffectHandler
import com.peekr.core.presentation.ui.model.UiSocialLoginProvider
import com.peekr.presentation.login.state.LoginUiEvent
import com.peekr.presentation.login.view.LoginScreen
import com.peekr.presentation.login.viewmodel.LoginViewModel

private const val TAG = "LoginRoute"

@Composable
fun LoginRoute(
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel = hiltViewModel(),
    onNavigateMain: () -> Unit,
    onNavigateRegister: (UiSocialLoginProvider, String) -> Unit,
) {
    val loginState by loginViewModel.loginState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context as Activity

    LaunchedEffect(loginState.error) {
        loginState.error?.let { error ->
            Toast.makeText(context, error.asString(context), Toast.LENGTH_SHORT).show()
            loginViewModel.onErrorMessageShown()
        }
    }

    LaunchedUiEffectHandler(
        effectFlow = loginViewModel.loginEvents,
        onConsumeEffect = { loginViewModel.onEventConsumed() },
        onEffect = { event ->
            when (event) {
                is LoginUiEvent.NavigateToRegister -> {
                    onNavigateRegister(event.provider, event.providerId)
                }

                LoginUiEvent.NavigateToMain -> {
                    onNavigateMain()
                }
            }
        },
    )

    // 네비게이션 이동 상태를 초기화하기 위해 사용
    DisposableEffect(Unit) {
        onDispose {
            loginViewModel.onResetNavigating()
        }
    }

    LoginScreen(
        modifier = modifier.fillMaxSize(),
        loginState = loginState,
        login = { provider ->
            if (!loginState.isNavigating) {
                when (provider) {
                    UiSocialLoginProvider.KAKAO -> startKakaoLogin(
                        activity = activity,
                        onSuccess = { loginViewModel.login(provider) },
                        onError = { error ->
                            Toast.makeText(context, error.asUiText().asString(context), Toast.LENGTH_SHORT).show()
                        },
                    )

                    else -> loginViewModel.login(provider)
                }
            }
        },
    )
}

/**
 * 카카오톡 설치 여부에 따라 로그인 방식 결정
 * - 카카오톡 설치 → loginWithKakaoTalk, 미설치 → loginWithKakaoAccount
 */
private fun startKakaoLogin(
    activity: Activity,
    onSuccess: () -> Unit,
    onError: (CommonErrorType) -> Unit,
) {
    if (UserApiClient.instance.isKakaoTalkLoginAvailable(activity)) {
        loginWithKakaoTalk(activity, onSuccess, onError)
    } else {
        loginWithKakaoAccount(activity, onSuccess, onError)
    }
}

/**
 * 카카오톡 앱으로 로그인
 * - 취소 → Cancellation 에러
 * - 그 외 실패 → 카카오 계정 로그인으로 폴백
 */
private fun loginWithKakaoTalk(
    activity: Activity,
    onSuccess: () -> Unit,
    onError: (CommonErrorType) -> Unit,
) {
    UserApiClient.instance.loginWithKakaoTalk(activity) { token, error ->
        when {
            error != null -> {
                AppLogger.i(TAG, "'Login with KakaoTalk' failed.")
                if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                    onError(CommonErrorType.SocialAuth.Cancellation)
                } else {
                    // 카카오톡 로그인 실패 시 카카오 계정으로 폴백
                    loginWithKakaoAccount(activity, onSuccess, onError)
                }
            }

            token != null -> {
                AppLogger.i(TAG, "'Login with KakaoTalk' succeeded.")
                onSuccess()
            }

            // token, error 둘 다 null인 예외 케이스
            else -> onError(CommonErrorType.SocialAuth.Unexpected(null))
        }
    }
}

/**
 * 카카오 계정(이메일)으로 로그인
 *
 * loginWithKakaoTalk 폴백 또는 카카오톡 미설치 시 직접 호출
 * - 취소 → Cancellation 에러
 * - 그 외 실패 → KakaoSignInError
 */
private fun loginWithKakaoAccount(
    activity: Activity,
    onSuccess: () -> Unit,
    onError: (CommonErrorType) -> Unit,
) {
    UserApiClient.instance.loginWithKakaoAccount(activity) { token, error ->
        when {
            error != null -> {
                AppLogger.i(TAG, "'Login with KakaoAccount' failed.")
                if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                    onError(CommonErrorType.SocialAuth.Cancellation)
                } else {
                    onError(CommonErrorType.SocialAuth.KakaoSignInError)
                }
            }

            token != null -> {
                AppLogger.i(TAG, "'Login with KakaoAccount' succeeded.")
                onSuccess()
            }

            // token, error 둘 다 null인 예외 케이스
            else -> onError(CommonErrorType.SocialAuth.Unexpected(null))
        }
    }
}
