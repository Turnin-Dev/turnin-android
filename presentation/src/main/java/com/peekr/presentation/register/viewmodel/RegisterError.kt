package com.peekr.presentation.register.viewmodel

import com.peekr.presentation.R
import com.peekr.presentation.common.util.UiText
import com.peekr.presentation.common.util.UiText.StringResource

/** 회원가입 화면에서 별도로 사용하는 에러 */
enum class RegisterError {
    /** 사용자 표시 ID를 사용할 수 없는 에러 */
    DisplayIdNotAvailable,

    /** 빈칸은 허용하지 않는다는 에러 */
    CantUseEmptyOrBlank,

    /** 변환된 사진이 null인 상황에 대한 에러 */
    ImageFileIsNull,
}

fun RegisterError.asUiText(): UiText =
    when (this) {
        RegisterError.DisplayIdNotAvailable -> {
            StringResource(R.string.register_screen_error_cant_use_display_id)
        }

        RegisterError.CantUseEmptyOrBlank -> {
            StringResource(R.string.register_screen_error_cant_use_empty_or_blank)
        }

        RegisterError.ImageFileIsNull -> {
            StringResource(R.string.register_screen_error_image_file_is_null)
        }
    }
