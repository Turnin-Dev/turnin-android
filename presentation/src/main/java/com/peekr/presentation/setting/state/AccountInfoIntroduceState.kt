package com.peekr.presentation.setting.state

import com.peekr.core.presentation.ui.util.UiText

/**
 * 계정 정보 - 소개글 상태
 *
 * @property introduce 소개글
 * @property introduceError 에러
 * @property isIntroduceValid 유효성 검사 성공 여부
 * @property loading 로딩 여부
 */
data class AccountInfoIntroduceState(
    val introduce: String = "",
    val introduceError: UiText? = null,
    val isIntroduceValid: Boolean = false,
    val loading: Boolean = false,
)
