package com.peekr.presentation.register.state

import androidx.compose.ui.graphics.ImageBitmap
import com.peekr.presentation.common.util.UiText

/**
 * 회원가입 - 프로필 상태 클래스
 */
data class RegisterProfileState(
    /** 프로필 사진 */
    val image: ImageBitmap? = null,
    /** 프로필 사진 원본 */
    val originalImage: ImageBitmap? = null,
    /** 소개 글 */
    val introduce: String = "",
    /** 소개 글 관련 에러 */
    val introduceError: UiText? = null,
    /** 다음 화면으로 이동할 수 있는지에 대한 여부 (Ex. `true`면 버튼 활성화) */
    val canNext: Boolean = false,
    /** 로딩 상태 */
    val loading: Boolean = false,
)
