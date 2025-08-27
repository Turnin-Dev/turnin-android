package com.peekr.presentation.login.state

import com.peekr.presentation.login.model.UiSocialLoginProvider
import com.peekr.presentation.shared.util.UiText

sealed interface LoginUiEvent {
    data class NavigateToRegister(
        val provider: UiSocialLoginProvider,
        val providerId: String,
    ) : LoginUiEvent

    data object NavigateToMain : LoginUiEvent
}

/**
 * 로그인 상태 클래스
 *
 * @param loginSuccess 로그인 성공 여부
 * @param loading 로그인 로딩
 * @param error 로그인 에러 메시지
 * @param event 로그인 UI 이벤트 [LoginUiEvent]
 */
data class LoginState(
    val loginSuccess: Boolean = false,
    val loading: Boolean = false,
    val error: UiText? = null,
    val event: LoginUiEvent? = null,
)
