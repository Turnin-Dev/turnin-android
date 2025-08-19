package com.peekr.presentation.register.state

import com.peekr.presentation.shared.util.UiText

/** 회원가입 상태 클래스 */
data class RegisterState(
    /** 사용자 표시 ID */
    val displayId: String = "",
    /** 사용자 이름 */
    val name: String = "",
    /** 사용자 프로필 이미지 url */
    val profileImageUrl: String = "",
    /**
     * 다음 화면으로 이동할 수 있는지에 대한 여부 (Ex. `true`면 버튼 활성화)
     *
     * 초기에만 true
     */
    val canNext: Boolean = true,
    /** 회원가입 도중 발생하는 모든 로딩 */
    val loading: Boolean = false,
    /** 회원가입 도중 발생하는 모든 에러 */
    val error: UiText? = null,
)
