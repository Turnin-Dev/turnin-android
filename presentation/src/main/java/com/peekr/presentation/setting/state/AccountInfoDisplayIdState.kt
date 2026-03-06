package com.peekr.presentation.setting.state

import com.peekr.core.presentation.ui.util.UiText

/**
 * 계정 정보 - 사용자 표시 ID 상태
 *
 * @property displayId 사용자 표시 ID
 * @property displayIdError 에러
 * @property isDisplayIdValid 유효성 검사 성공 여부
 * @property loading 로딩 여부
 */
data class AccountInfoDisplayIdState(
    val displayId: String = "",
    val displayIdError: UiText? = null,
    val isDisplayIdValid: Boolean = false,
    val loading: Boolean = false,
)
