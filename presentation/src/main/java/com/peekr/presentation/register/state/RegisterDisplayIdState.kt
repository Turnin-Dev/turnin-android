package com.peekr.presentation.register.state

import com.peekr.core.presentation.ui.util.UiText

/**
 * 회원가입 - 사용자 표시 ID 상태 클래스
 */
data class RegisterDisplayIdState(
    /** 사용자 표시 ID */
    val displayId: String = "",
    /** 사용자 표시 ID 관련 에러 */
    val displayIdError: UiText? = null,
    /** 다음 화면으로 이동할 수 있는지에 대한 여부 (Ex. `true`면 버튼 활성화) */
    val canNext: Boolean = false,
    /** 로딩 상태 */
    val loading: Boolean = false,
)
