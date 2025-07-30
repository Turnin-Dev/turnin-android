package com.peekr.presentation.login.state

import com.peekr.presentation.shared.util.UiText

/**
 * 로그인 상태 클래스
 *
 * @param loginSuccess 로그인 성공 여부
 * @param loading 로그인 로딩
 * @param error 로그인 에러 메시지
 */
data class LoginState(
    val loginSuccess: Boolean = false,
    val loading: Boolean = false,
    val error: UiText? = null,
)
