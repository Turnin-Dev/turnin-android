package com.peekr.presentation.profile.state

import com.peekr.core.presentation.ui.util.UiText

/**
 * 키워드 관련 텍스트 필드 UI 상태
 *
 * @param value 키워드 관련 텍스트 (키워드 및 키워드 내용)
 * @param error 에러
 */
data class KeywordTextFieldState(
    val value: String = "",
    val error: UiText? = null,
)
