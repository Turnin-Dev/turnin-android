package com.peekr.presentation.register.viewmodel

import com.peekr.presentation.R
import com.peekr.presentation.shared.util.UiText
import com.peekr.presentation.shared.util.UiText.StringResource

/** 회원가입 화면에서 별도로 사용하는 에러 */
enum class RegisterError {
    /** 사용자 표시 ID를 사용할 수 없는 에러 */
    DisplayIdNotAvailable,

    /** 빈칸은 허용하지 않는다는 에러 */
    CantUseEmptyOrBlank,
}

fun RegisterError.asUiText(): UiText =
    when (this) {
        RegisterError.DisplayIdNotAvailable -> {
            StringResource(R.string.register_screen_error_cant_use_display_id)
        }

        RegisterError.CantUseEmptyOrBlank -> {
            StringResource(R.string.register_screen_error_cant_use_empty_or_blank)
        }
    }
