package com.peekr.presentation.login.state

import com.peekr.domain.account.model.SocialLoginProvider
import com.peekr.domain.account.model.UserUID
import com.peekr.presentation.shared.util.UiText

sealed interface LoginUiEvent {
    data class NavigateToRegister(
        val provider: SocialLoginProvider,
        val providerId: UserUID,
    ) : LoginUiEvent

    data object NavigateToMain : LoginUiEvent
}

/**
 * 로그인 상태 클래스
 *
 * @param loginSuccess 로그인 성공 여부
 * @param loading 로그인 로딩
 * @param error 로그인 에러 메시지
 * @param [LoginUiEvent]
 */
data class LoginState(
    val loginSuccess: Boolean = false,
    val loading: Boolean = false,
    val error: UiText? = null,
    val event: LoginUiEvent? = null,
)
