package com.peekr.presentation.shared.util.error

import com.peekr.domain.account.validation.RegisterValidationError
import com.peekr.presentation.R
import com.peekr.presentation.shared.util.UiText

fun RegisterValidationError.asUiText(): UiText = when (this) {
    RegisterValidationError.EXCEEDS_MAX_LENGTH -> {
        UiText.StringResource(R.string.register_screen_validation_exceeds_max_length)
    }

    RegisterValidationError.ONLY_ALPHANUMERIC_UNDERSCORE -> {
        UiText.StringResource(R.string.register_screen_validation_only_al_nu_und)
    }

    RegisterValidationError.ONLY_ALPHANUMERIC_HANGUL -> {
        UiText.StringResource(R.string.register_screen_validation_only_al_nu_han)
    }
}
