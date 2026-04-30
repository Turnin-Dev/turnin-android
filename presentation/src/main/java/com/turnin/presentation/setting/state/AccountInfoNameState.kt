package com.turnin.presentation.setting.state

import com.turnin.core.presentation.ui.util.UiText

/**
 * 계정 정보 - 사용자 명 상태
 *
 * @property name 사용자 명
 * @property nameError 에러
 * @property isNameValid 유효성 검사 성공 여부
 * @property loading 로딩 여부
 */
data class AccountInfoNameState(
    val name: String = "",
    val nameError: UiText? = null,
    val isNameValid: Boolean = false,
    val loading: Boolean = false,
)
