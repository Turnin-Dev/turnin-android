package com.peekr.presentation.register.state

/**
 * 회원가입 이벤트 상태 클래스
 *
 * (이 데이터 클래스는 이벤트를 상태처럼 모델링한 클래스입니다.)
 */
data class RegisterEventState(
    /** 다음 화면으로 이동 이벤트 */
    val navigateToNextScreen: Boolean = false,
    /** 사진 편집 화면으로 이동 이벤트 */
    val navigateToCropImageScreen: Boolean = false,
)
